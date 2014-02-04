package net.sourceforge.fenixedu.applicationTier.Servico.space;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.spaceManager.CreateBlueprintSubmissionBean;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.space.Blueprint;
import net.sourceforge.fenixedu.domain.space.BlueprintFile;
import net.sourceforge.fenixedu.domain.space.Space;
import net.sourceforge.fenixedu.domain.space.SpaceInformation;
import pt.utl.ist.fenix.tools.util.FileUtils;

public abstract class BlueprintVersionManagmentService {

    protected Space getSpace(CreateBlueprintSubmissionBean blueprintSubmissionBean) throws FenixServiceException {
        final SpaceInformation spaceInformation = blueprintSubmissionBean.getSpaceInformation();
        final Space space = spaceInformation.getSpace();
        if (space == null) {
            throw new FenixServiceException("error.blueprint.submission.no.space");
        }
        return space;
    }

    protected void editBlueprintVersion(CreateBlueprintSubmissionBean blueprintSubmissionBean, final Space space,
            final Person person, final Blueprint blueprint) throws IOException {

        final String filename =
                blueprintSubmissionBean.getSpaceInformation().getExternalId() + String.valueOf(System.currentTimeMillis());
        final byte[] contents = readInputStream(blueprintSubmissionBean.getInputStream());

        final String displayName = blueprintSubmissionBean.getFilename();
//        final BlueprintFile blueprintFile =
//                new BlueprintFile(blueprint, filename, displayName,
//                        new RoleGroup(Role.getRoleByRoleType(RoleType.SPACE_MANAGER)), contents);
//        blueprintFile.setContent(new ByteArray(contents));

        final BlueprintFile blueprintFile = new BlueprintFile(blueprint, filename, displayName, null, contents);
        blueprint.setBlueprintFile(blueprintFile);
    }

    private byte[] readInputStream(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileUtils.copy(inputStream, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
