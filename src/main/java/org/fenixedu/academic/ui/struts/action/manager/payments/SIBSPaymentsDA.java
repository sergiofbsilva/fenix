/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic.
 *
 * FenixEdu Academic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.ui.struts.action.manager.payments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accounting.PaymentCode;
import org.fenixedu.academic.domain.accounting.PaymentCodeMapping;
import org.fenixedu.academic.domain.accounting.PaymentCodeState;
import org.fenixedu.academic.domain.accounting.SibsPaymentFileProcessReport;
import org.fenixedu.academic.domain.accounting.paymentCodes.EventPaymentCode;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.dto.accounting.sibsPaymentFileProcessReport.SibsPaymentFileProcessReportDTO;
import org.fenixedu.academic.predicate.AccessControl;
import org.fenixedu.academic.ui.struts.action.base.FenixDispatchAction;
import org.fenixedu.academic.ui.struts.action.manager.ManagerApplications.ManagerPaymentsApp;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.sibs.incomming.SibsIncommingPaymentFile;
import org.fenixedu.academic.util.sibs.incomming.SibsIncommingPaymentFileDetailLine;
import org.fenixedu.bennu.core.groups.Group;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.struts.annotations.Forward;
import org.fenixedu.bennu.struts.annotations.Forwards;
import org.fenixedu.bennu.struts.annotations.Mapping;
import org.fenixedu.bennu.struts.portal.EntryPoint;
import org.fenixedu.bennu.struts.portal.StrutsFunctionality;
import org.fenixedu.commons.StringNormalizer;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.messaging.core.domain.Message;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import pt.ist.fenixWebFramework.renderers.utils.RenderUtils;
import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

@StrutsFunctionality(app = ManagerPaymentsApp.class, path = "sibs-payments", titleKey = "label.payments.uploadPaymentsFile")
@Mapping(path = "/SIBSPayments", module = "manager")
@Forwards({ @Forward(name = "prepareUploadSIBSPaymentFiles", path = "/manager/payments/prepareUploadSIBSPaymentFiles.jsp") })
public class SIBSPaymentsDA extends FenixDispatchAction {

    static private final String PAYMENT_FILE_EXTENSION = "INP";
    static private final String ZIP_FILE_EXTENSION = "ZIP";

    static public class UploadBean implements Serializable {
        private static final long serialVersionUID = 3625314688141697558L;

        private transient InputStream inputStream;

        private String filename;

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = StringNormalizer.normalize(filename);
        }
    }

    private class ProcessResult {

        private String requestorUsername;
        private boolean processFailed = false;
        private List<String> messages = new ArrayList<>();

        public ProcessResult(String requestorUsername) {
            this.requestorUsername = requestorUsername;
        }

        public void addMessage(String message, String... args) {
            messages.add(BundleUtil.getString(Bundle.MANAGER, message, args));
        }

        public void addError(String message, String... args) {
            messages.add(BundleUtil.getString(Bundle.MANAGER, message, args));
            reportFailure();
        }

        public String toString() {
            return String.join("\n", messages);
        }

        protected void reportFailure() {
            processFailed = true;
        }

        public boolean hasFailed() {
            return processFailed;
        }
    }

    @EntryPoint
    public ActionForward prepareUploadSIBSPaymentFiles(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        UploadBean bean = getRenderedObject("uploadBean");
        RenderUtils.invalidateViewState("uploadBean");
        if (bean == null) {
            bean = new UploadBean();
        }

        request.setAttribute("uploadBean", bean);
        return mapping.findForward("prepareUploadSIBSPaymentFiles");
    }

    public ActionForward uploadSIBSPaymentFiles(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        UploadBean bean = getRenderedObject("uploadBean");
        RenderUtils.invalidateViewState("uploadBean");

        if (bean == null) {
            return prepareUploadSIBSPaymentFiles(mapping, form, request, response);
        }

        if (StringUtils.endsWithIgnoreCase(bean.getFilename(), PAYMENT_FILE_EXTENSION)) {
            InputStream inputStream = bean.getInputStream();
            File dir = Files.createTempDir();
            File tmp = new File(dir, bean.getFilename());
            tmp.deleteOnExit();

            try (OutputStream out = new FileOutputStream(tmp)) {
                ByteStreams.copy(inputStream, out);
            } finally {
                inputStream.close();
            }

            final Person requestor = AccessControl.getPerson();

            Thread runner = new Thread(() -> {
                processFile(tmp, requestor);
            });

            runner.start();
            addActionMessage("message", request, "label.payments.uploadPaymentsFile.in.progress", bean.getFilename());
        } else {
            addActionMessage("error", request, "error.manager.SIBS.notSupportedExtension", bean.getFilename());
        }
        return prepareUploadSIBSPaymentFiles(mapping, form, request, response);
    }

    private static String getMessage(Exception ex) {
        String message = (ex.getMessage() == null) ? ex.getClass().getSimpleName() : ex.getMessage();
        String[] args = null;
        if (ex instanceof DomainException) {
            args = ((DomainException) ex).getArgs();
        }
        return BundleUtil.getString(Bundle.MANAGER, message, args);
    }

    @Atomic(mode = Atomic.TxMode.READ)
    private void processFile(final File file, Person requestorPerson) {
        final ProcessResult result = new ProcessResult(requestorPerson.getUser().getUsername());
        final String filename = file.getName();
        
        result.addMessage("label.manager.SIBS.processingFile", filename);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            final SibsIncommingPaymentFile sibsFile = SibsIncommingPaymentFile.parse(filename, fileInputStream);

            result.addMessage("label.manager.SIBS.linesFound", String.valueOf(sibsFile.getDetailLines().size()));
            result.addMessage("label.manager.SIBS.startingProcess");

            sibsFile.getDetailLines().forEach(detailLine -> {
                try {
                    processCode(detailLine, requestorPerson, result);
                } catch (Exception e) {
                    result.addError("error.manager.SIBS.processException", detailLine.getCode(), getMessage(e));
                }
            });

            result.addMessage("label.manager.SIBS.creatingReport");

            if (result.hasFailed()) {
                result.addError("error.manager.SIBS.nonProcessedCodes");
            } else if (SibsPaymentFileProcessReport.hasAny(sibsFile.getWhenProcessedBySibs(), sibsFile.getVersion())) {
                result.addMessage("warning.manager.SIBS.reportAlreadyProcessed");
            } else {
                try {
                    createSibsFileReport(sibsFile, result);
                } catch (Exception ex) {
                    result.addError("error.manager.SIBS.reportException", getMessage(ex));
                }
            }

            result.addMessage("label.manager.SIBS.done");

        } catch (IOException ex) {
            result.addError("error.manager.SIBS.reportException", getMessage(ex));
        } finally {
            FenixFramework.atomic(() -> {
                Message.fromSystem().preferredLocale(I18N.getLocale())
                        .subject(BundleUtil.getString(Bundle.MANAGER, "label.payments.uploadPaymentsFile.process.subject",
                                filename))
                        .textBody(result.toString())
                        .to(Group.users(requestorPerson.getUser()))
                        .singleBcc(CoreConfiguration.getConfiguration().defaultSupportEmailAddress())
                        .send();
            });
        }
    }

    private void processCode(SibsIncommingPaymentFileDetailLine detailLine, Person person, ProcessResult result)
            throws Exception {

        final PaymentCode paymentCode = getPaymentCode(detailLine, result);

        if (paymentCode == null) {
            result.addMessage("error.manager.SIBS.codeNotFound", detailLine.getCode());
            throw new Exception();
        }

        if (!(paymentCode instanceof EventPaymentCode)) {
            result.addMessage("warning.manager.SIBS.outdated.code", paymentCode.getCode(), paymentCode.getExternalId());
            return;
        }

        if (paymentCode.getState() == PaymentCodeState.INVALID) {
            result.addMessage("warning.manager.SIBS.invalidCode", paymentCode.getCode());
        }

        if (paymentCode.isProcessed() && paymentCode.matches(detailLine)) {
            result.addMessage("warning.manager.SIBS.codeAlreadyProcessed", paymentCode.getCode());
            return;
        }

        paymentCode.process(person, detailLine);

    }

    private void createSibsFileReport(SibsIncommingPaymentFile sibsIncomingPaymentFile, ProcessResult result) throws Exception {
        final SibsPaymentFileProcessReportDTO reportDTO = new SibsPaymentFileProcessReportDTO(sibsIncomingPaymentFile);
        for (final SibsIncommingPaymentFileDetailLine detailLine : sibsIncomingPaymentFile.getDetailLines()) {
            ExecutionYear executionYear = ExecutionYear.readByDateTime(detailLine.getWhenOccuredTransaction());
            PaymentCode paymentCodeToProcess = getPaymentCodeToProcess(getPaymentCode(detailLine, result), executionYear, result);
            reportDTO.addAmount(detailLine, paymentCodeToProcess);
        }
        SibsPaymentFileProcessReport.create(reportDTO);
        result.addMessage("label.manager.SIBS.reportCreated");
    }

    // Remove in next major
    @Deprecated
    private PaymentCode getPaymentCodeToProcess(final PaymentCode paymentCode, ExecutionYear executionYear, ProcessResult result) {

        final PaymentCodeMapping mapping = paymentCode.getOldPaymentCodeMapping(executionYear);

        final PaymentCode codeToProcess;
        if (mapping != null) {

            result.addMessage("warning.manager.SIBS.foundMapping", paymentCode.getCode(), mapping.getNewPaymentCode().getCode());
            result.addMessage("warning.manager.SIBS.invalidating", paymentCode.getCode());

            codeToProcess = mapping.getNewPaymentCode();
            paymentCode.setState(PaymentCodeState.INVALID);

        } else {
            codeToProcess = paymentCode;
        }

        return codeToProcess;
    }

    private PaymentCode getPaymentCode(final SibsIncommingPaymentFileDetailLine detailLine, ProcessResult result) {
        return getPaymentCode(detailLine.getCode(), result);
    }

    private PaymentCode getPaymentCode(final String code, ProcessResult result) {
        /*
         * TODO:
         *
         * 09/07/2009 - Payments are not related only to students. readAll() may
         * be heavy to get the PaymentCode.
         *
         *
         * Ask Nadir and Joao what is best way to deal with PaymentCode
         * retrieval.
         */

        return PaymentCode.readByCode(code);
    }
}
