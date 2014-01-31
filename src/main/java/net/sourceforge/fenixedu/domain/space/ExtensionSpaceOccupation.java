package net.sourceforge.fenixedu.domain.space;

import net.sourceforge.fenixedu.domain.exceptions.DomainException;
import net.sourceforge.fenixedu.domain.material.Extension;
import net.sourceforge.fenixedu.injectionCode.FenixDomainObjectActionLogAnnotation;

import org.fenixedu.bennu.core.domain.groups.Group;
import org.joda.time.YearMonthDay;

public class ExtensionSpaceOccupation extends ExtensionSpaceOccupation_Base {

    @FenixDomainObjectActionLogAnnotation(actionName = "Created extension occupation", parameters = { "space", "extension",
            "begin", "end" })
    public ExtensionSpaceOccupation(Space space, Extension extension, YearMonthDay begin, YearMonthDay end) {
        super();
        setResource(space);
        setExtension(extension);
        checkExtensionSpaceOccupationIntersection(begin, end, extension);
        super.setBegin(begin);
        super.setEnd(end);
    }

    @FenixDomainObjectActionLogAnnotation(actionName = "Edited extension occupation", parameters = { "begin", "end" })
    public void setOccupationInterval(final YearMonthDay begin, final YearMonthDay end) {
//        check(this, SpacePredicates.checkPermissionsToManageExtensionSpaceOccupations);
        checkExtensionSpaceOccupationIntersection(begin, end, getExtension());
        super.setBegin(begin);
        super.setEnd(end);
    }

    @Override
    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted extension occupation", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkPermissionsToManageExtensionSpaceOccupations);
        super.delete();
    }

    @Override
    public void setMaterial(Extension material) {
        if (material == null || !(material instanceof Extension)) {
            throw new DomainException("error.extensionSpaceOccupation.no.extension");
        }
        super.setMaterial(material);
    }

    public Extension getExtension() {
        return (Extension) getMaterial();
    }

    public void setExtension(Extension extension) {
        setMaterial(extension);
    }

    @Override
    public void setBegin(YearMonthDay begin) {
        throw new DomainException("error.invalid.operation");
    }

    @Override
    public void setEnd(YearMonthDay end) {
        throw new DomainException("error.invalid.operation");
    }

    @Override
    public Group getAccessGroup() {
        return getSpace().getExtensionOccupationsAccessGroupWithChainOfResponsibility();
    }

    private void checkExtensionSpaceOccupationIntersection(YearMonthDay begin, YearMonthDay end, Extension extension) {
        checkBeginDateAndEndDate(begin, end);
        for (ExtensionSpaceOccupation materialSpaceOccupation : extension.getMaterialSpaceOccupationsSet()) {
            if (!materialSpaceOccupation.equals(this) && materialSpaceOccupation.getSpace().equals(getSpace())
                    && ((ExtensionSpaceOccupation) materialSpaceOccupation).occupationsIntersection(begin, end)) {
                throw new DomainException("error.extensionSpaceOccupation.intersection");
            }
        }
    }

    private boolean occupationsIntersection(YearMonthDay possibleBegin, YearMonthDay possibleEnd) {
        return ((possibleEnd == null || !getBegin().isAfter(possibleEnd)) && (getEnd() == null || !getEnd().isBefore(
                possibleBegin)));
    }

    private void checkBeginDateAndEndDate(YearMonthDay begin, YearMonthDay end) {
        if (begin == null) {
            throw new DomainException("error.personSpaceOccupation.no.beginDate");
        }
        if (end != null && !end.isAfter(begin)) {
            throw new DomainException("error.begin.after.end");
        }
    }
}
