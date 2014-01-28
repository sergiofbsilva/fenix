package net.sourceforge.fenixedu.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.fenixedu.dataTransferObject.spaceManager.FindSpacesBean.SpacesSearchCriteriaType;
import net.sourceforge.fenixedu.domain.DomainObjectActionLog;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.Lesson;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.WrittenEvaluation;
import net.sourceforge.fenixedu.domain.exception.SpaceDomainException;
import net.sourceforge.fenixedu.domain.resource.Resource;
import net.sourceforge.fenixedu.domain.resource.ResourceAllocation;
import net.sourceforge.fenixedu.domain.space.Blueprint;
import net.sourceforge.fenixedu.domain.space.Building;
import net.sourceforge.fenixedu.domain.space.BuildingInformation;
import net.sourceforge.fenixedu.domain.space.Campus;
import net.sourceforge.fenixedu.domain.space.CampusInformation;
import net.sourceforge.fenixedu.domain.space.Floor;
import net.sourceforge.fenixedu.domain.space.FloorInformation;
import net.sourceforge.fenixedu.domain.space.LessonSpaceOccupation;
import net.sourceforge.fenixedu.domain.space.PersonSpaceOccupation;
import net.sourceforge.fenixedu.domain.space.Room;
import net.sourceforge.fenixedu.domain.space.RoomClassification;
import net.sourceforge.fenixedu.domain.space.RoomInformation;
import net.sourceforge.fenixedu.domain.space.RoomSubdivision;
import net.sourceforge.fenixedu.domain.space.RoomSubdivisionInformation;
import net.sourceforge.fenixedu.domain.space.Space;
import net.sourceforge.fenixedu.domain.space.SpaceAttendances;
import net.sourceforge.fenixedu.domain.space.SpaceResponsibility;
import net.sourceforge.fenixedu.domain.space.UnitSpaceOccupation;
import net.sourceforge.fenixedu.domain.time.calendarStructure.AcademicInterval;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.StringNormalizer;
import org.joda.time.DateTime;

import pt.ist.fenixframework.DomainObject;

public class SpaceBridge {

    public static String[] getIdentificationWords(String name) {
        String[] identificationWords = null;
        if (name != null && !StringUtils.isEmpty(name.trim())) {
            identificationWords = StringNormalizer.normalize(name).trim().split(" ");
        }
        return identificationWords;
    }

    public static Set<Space> findSpaces(String labelToSearch, Campus campus, Building building,
            SpacesSearchCriteriaType searchType) {

        Set<Space> result = new TreeSet<Space>(Space.COMPARATOR_BY_NAME_FLOOR_BUILDING_AND_CAMPUS);

        if (searchType != null
                && (campus != null || building != null || (labelToSearch != null && !StringUtils.isEmpty(labelToSearch.trim())))) {

            String[] labelWords = getIdentificationWords(labelToSearch);
            Set<ExecutionCourse> executionCoursesToTest = searchExecutionCoursesByName(searchType, labelWords);
            Collection<Person> personsToTest = searchPersonsByName(searchType, labelToSearch);

            for (Resource resource : Bennu.getInstance().getResourcesSet()) {

                if (resource.isSpace() && ((Space) resource).isActive() && !resource.equals(campus) && !resource.equals(building)) {

                    Space space = (Space) resource;

                    if (labelWords != null) {

                        boolean toAdd = false;

                        switch (searchType) {

                        case SPACE:
                            toAdd = space.verifyNameEquality(labelWords);
                            break;

                        case PERSON:
                            for (Person person : personsToTest) {
                                if (person.getActivePersonSpaces().contains(resource)) {
                                    toAdd = true;
                                    break;
                                }
                            }
                            break;

                        case EXECUTION_COURSE:
                            for (ExecutionCourse executionCourse : executionCoursesToTest) {
                                if (executionCourse.getAllRooms().contains(resource)) {
                                    toAdd = true;
                                    break;
                                }
                            }
                            break;

                        case WRITTEN_EVALUATION:
                            for (ExecutionCourse executionCourse : executionCoursesToTest) {
                                SortedSet<WrittenEvaluation> writtenEvaluations = executionCourse.getWrittenEvaluations();
                                for (WrittenEvaluation writtenEvaluation : writtenEvaluations) {
                                    if (writtenEvaluation.getAssociatedRooms().contains(resource)) {
                                        toAdd = true;
                                        break;
                                    }
                                }
                                if (toAdd) {
                                    break;
                                }
                            }
                            break;

                        default:
                            break;
                        }

                        if (!toAdd) {
                            continue;
                        }
                    }

                    if (building != null) {
                        Building spaceBuilding = space.getSpaceBuilding();
                        if (spaceBuilding == null || !spaceBuilding.equals(building)) {
                            continue;
                        }
                    } else if (campus != null) {
                        Campus spaceCampus = space.getSpaceCampus();
                        if (spaceCampus == null || !spaceCampus.equals(campus)) {
                            continue;
                        }
                    }

                    result.add(space);
                }
            }
        }
        return result;
    }

    private static Collection<Person> searchPersonsByName(SpacesSearchCriteriaType searchType, String labelToSearch) {
        if (labelToSearch != null && !StringUtils.isEmpty(labelToSearch) && searchType.equals(SpacesSearchCriteriaType.PERSON)) {
            return Person.findPerson(labelToSearch);
        }
        return Collections.EMPTY_LIST;
    }

    private static Set<ExecutionCourse> searchExecutionCoursesByName(SpacesSearchCriteriaType searchType, String[] labelWords) {
        Set<ExecutionCourse> executionCoursesToTest = null;
        if (labelWords != null
                && (searchType.equals(SpacesSearchCriteriaType.EXECUTION_COURSE) || searchType
                        .equals(SpacesSearchCriteriaType.WRITTEN_EVALUATION))) {
            executionCoursesToTest = new HashSet<ExecutionCourse>();
            for (ExecutionCourse executionCourse : ExecutionSemester.readActualExecutionSemester()
                    .getAssociatedExecutionCoursesSet()) {
                if (executionCourse.verifyNameEquality(labelWords)) {
                    executionCoursesToTest.add(executionCourse);
                }
            }
        }
        return executionCoursesToTest;
    }

    public static Set<DomainObjectActionLog> getListOfChangesInSpacesOrderedByInstant() {
        Set<Class<? extends DomainObject>> classs = new HashSet<Class<? extends DomainObject>>();
        User loggedPerson = Authenticate.getUser();
        if (Space.personIsSpacesAdministrator(loggedPerson)) {
            classs.add(Room.class);
            classs.add(Floor.class);
            classs.add(Campus.class);
            classs.add(Building.class);
            classs.add(Blueprint.class);
            classs.add(RoomSubdivision.class);
            classs.add(RoomInformation.class);
            classs.add(FloorInformation.class);
            classs.add(CampusInformation.class);
            classs.add(RoomClassification.class);
            classs.add(BuildingInformation.class);
            classs.add(UnitSpaceOccupation.class);
            classs.add(SpaceResponsibility.class);
            classs.add(PersonSpaceOccupation.class);
            classs.add(RoomSubdivisionInformation.class);
            return DomainObjectActionLog.readDomainObjectActionLogsOrderedByInstant(classs);
        }
        return new HashSet<DomainObjectActionLog>();
    }

    public static List<UnitSpaceOccupation> getUnitSpaceOccupations(Space space) {
        List<UnitSpaceOccupation> unitSpaceOccupations = new ArrayList<UnitSpaceOccupation>();
        for (ResourceAllocation allocation : space.getResourceAllocationsSet()) {
            if (allocation.isUnitSpaceOccupation()) {
                unitSpaceOccupations.add((UnitSpaceOccupation) allocation);
            }
        }
        return unitSpaceOccupations;
    }

    public static List<PersonSpaceOccupation> getPersonSpaceOccupations(Space space) {
        List<PersonSpaceOccupation> personSpaceOccupations = new ArrayList<PersonSpaceOccupation>();
        for (ResourceAllocation allocation : space.getResourceAllocationsSet()) {
            if (allocation.isPersonSpaceOccupation()) {
                personSpaceOccupations.add((PersonSpaceOccupation) allocation);
            }
        }
        return personSpaceOccupations;
    }

    public static int currentAttendaceCount(Space space) {
        int occupants = space.getCurrentAttendanceSet().size();
        for (Space innerSpace : space.getActiveContainedSpaces()) {
            occupants += currentAttendaceCount(innerSpace);
        }
        return occupants;
    }

    public static boolean canAddAttendance(Space space) {
        return currentAttendaceCount(space) < space.getSpaceInformation().getCapacity();
    }

    public static SpaceAttendances addAttendance(Space space, Person person, String responsibleUsername) {
        if (person == null) {
            return null;
        }
        if (!canAddAttendance(space)) {
            throw new SpaceDomainException("error.space.maximumAttendanceExceeded");
        }
        SpaceAttendances attendance = new SpaceAttendances(person.getIstUsername(), responsibleUsername, new DateTime());
        space.addCurrentAttendance(attendance);
        space.addPastAttendances(attendance);
        return attendance;
    }

    @Deprecated
    public static List<Lesson> getAssociatedLessons(final Space space, final ExecutionSemester executionSemester) {
        final List<Lesson> lessons = new ArrayList<Lesson>();
        for (ResourceAllocation spaceOccupation : space.getResourceAllocations()) {
            if (spaceOccupation.isLessonSpaceOccupation()) {
                LessonSpaceOccupation roomOccupation = (LessonSpaceOccupation) spaceOccupation;
                final Lesson lesson = roomOccupation.getLesson();
                if (lesson.getExecutionPeriod() == executionSemester) {
                    lessons.add(lesson);
                }
            }
        }
        return lessons;
    }

    public static List<Lesson> getAssociatedLessons(final Space space, AcademicInterval academicInterval) {
        final List<Lesson> lessons = new ArrayList<Lesson>();
        for (ResourceAllocation spaceOccupation : space.getResourceAllocations()) {
            if (spaceOccupation.isLessonSpaceOccupation()) {
                LessonSpaceOccupation roomOccupation = (LessonSpaceOccupation) spaceOccupation;
                final Lesson lesson = roomOccupation.getLesson();
                if (lesson.getAcademicInterval().equals(academicInterval)) {
                    lessons.add(lesson);
                }
            }
        }
        return lessons;
    }

}
