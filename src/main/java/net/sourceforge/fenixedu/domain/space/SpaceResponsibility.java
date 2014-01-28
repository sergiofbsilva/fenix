package net.sourceforge.fenixedu.domain.space;

import java.text.Collator;
import java.util.Comparator;

import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.organizationalStructure.Party;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;
import net.sourceforge.fenixedu.domain.resource.Resource;
import net.sourceforge.fenixedu.domain.resource.ResourceResponsibility;
import net.sourceforge.fenixedu.util.DomainObjectUtil;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.joda.time.YearMonthDay;

public class SpaceResponsibility extends SpaceResponsibility_Base {

    public static final Comparator<SpaceResponsibility> COMPARATOR_BY_UNIT_NAME_AND_RESPONSIBILITY_INTERVAL =
            new ComparatorChain();
    static {
        ((ComparatorChain) COMPARATOR_BY_UNIT_NAME_AND_RESPONSIBILITY_INTERVAL).addComparator(new BeanComparator("begin",
                new NullComparator()));
        ((ComparatorChain) COMPARATOR_BY_UNIT_NAME_AND_RESPONSIBILITY_INTERVAL).addComparator(new BeanComparator("unit.name",
                Collator.getInstance()));
        ((ComparatorChain) COMPARATOR_BY_UNIT_NAME_AND_RESPONSIBILITY_INTERVAL).addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
    }

//    @FenixDomainObjectActionLogAnnotation(actionName = "Created space responsibility", parameters = { "space", "unit", "begin",
//            "end" })
    public SpaceResponsibility(final Space space, final Unit unit, final YearMonthDay begin, final YearMonthDay end) {

        super();
        setSpace(space);
        setUnit(unit);
        checkSpaceResponsabilityIntersection(begin, end, getUnit(), getSpace());
        super.setBegin(begin);
        super.setEnd(end);
    }

//    @FenixDomainObjectActionLogAnnotation(actionName = "Edited space responsibility", parameters = { "begin", "end" })
    public void setSpaceResponsibilityInterval(final YearMonthDay begin, final YearMonthDay end) {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageResponsabilityUnits);
        checkSpaceResponsabilityIntersection(begin, end, getUnit(), getSpace());
        super.setBegin(begin);
        super.setEnd(end);
    }

    @Override
//    @FenixDomainObjectActionLogAnnotation(actionName = "Deleted space responsibility", parameters = {})
    public void delete() {
//        check(this, SpacePredicates.checkIfLoggedPersonHasPermissionsToManageResponsabilityUnits);
        super.delete();
    }

    @Override
    public boolean isSpaceResponsibility() {
        return true;
    }

    @Override
    public void setBegin(YearMonthDay begin) {
        throw new SpaceDomainException("error.invalid.operation");
    }

    @Override
    public void setEnd(YearMonthDay end) {
        throw new SpaceDomainException("error.invalid.operation");
    }

    @Override
    public void setResource(Resource resource) {
        if (resource == null || !resource.isSpace()) {
            throw new SpaceDomainException("error.space.responsability.no.space");
        }
        super.setResource(resource);
    }

    @Override
    public void setParty(Party party) {
        if (party == null || !party.isUnit() || party.isAggregateUnit()) {
            throw new SpaceDomainException("error.space.responsability.no.unit");
        }
        super.setParty(party);
    }

    public void setUnit(Unit unit) {
        setParty(unit);
    }

    public Unit getUnit() {
        return (Unit) getParty();
    }

    public void setSpace(Space space) {
        setResource(space);
    }

    public Space getSpace() {
        return (Space) getResource();
    }

    private boolean checkIntersections(YearMonthDay begin, YearMonthDay end) {
        return ((end == null || !getBegin().isAfter(end)) && (getEnd() == null || !getEnd().isBefore(begin)));
    }

    private void checkBeginDateAndEndDate(YearMonthDay beginDate, YearMonthDay endDate) {
        if (beginDate == null) {
            throw new SpaceDomainException("error.spaceResponsability.no.begin");
        }
        if (endDate != null && endDate.isBefore(beginDate)) {
            throw new SpaceDomainException("error.begin.after.end");
        }
    }

    private void checkSpaceResponsabilityIntersection(final YearMonthDay begin, final YearMonthDay end, Unit unit, Space space) {
        checkBeginDateAndEndDate(begin, end);
        for (ResourceResponsibility resourceResponsibility : space.getResourceResponsibilitySet()) {
            if (resourceResponsibility.isSpaceResponsibility() && !resourceResponsibility.equals(this)
                    && resourceResponsibility.getParty().equals(unit)
                    && ((SpaceResponsibility) resourceResponsibility).checkIntersections(begin, end)) {
                throw new SpaceDomainException("error.spaceResponsibility.unit.intersection");
            }
        }
    }
}
