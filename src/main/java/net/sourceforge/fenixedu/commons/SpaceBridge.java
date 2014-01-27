package net.sourceforge.fenixedu.commons;


public class SpaceBridge {

//    public static Set<Space> findSpaces(String labelToSearch, Campus campus, Building building,
//            SpacesSearchCriteriaType searchType) {
//
//        Set<Space> result = new TreeSet<Space>(Space.COMPARATOR_BY_NAME_FLOOR_BUILDING_AND_CAMPUS);
//
//        if (searchType != null
//                && (campus != null || building != null || (labelToSearch != null && !StringUtils.isEmpty(labelToSearch.trim())))) {
//
//            String[] labelWords = getIdentificationWords(labelToSearch);
//            Set<ExecutionCourse> executionCoursesToTest = searchExecutionCoursesByName(searchType, labelWords);
//            Collection<User> personsToTest = searchPersonsByName(searchType, labelToSearch);
//
//            for (Resource resource : Bennu.getInstance().getResourcesSet()) {
//
//                if (resource.isSpace() && ((Space) resource).isActive() && !resource.equals(campus) && !resource.equals(building)) {
//
//                    Space space = (Space) resource;
//
//                    if (labelWords != null) {
//
//                        boolean toAdd = false;
//
//                        switch (searchType) {
//
//                        case SPACE:
//                            toAdd = space.verifyNameEquality(labelWords);
//                            break;
//
//                        case PERSON:
//                            for (User person : personsToTest) {
//                                if (person.getActivePersonSpaces().contains(resource)) {
//                                    toAdd = true;
//                                    break;
//                                }
//                            }
//                            break;
//
//                        case EXECUTION_COURSE:
//                            for (ExecutionCourse executionCourse : executionCoursesToTest) {
//                                if (executionCourse.getAllRooms().contains(resource)) {
//                                    toAdd = true;
//                                    break;
//                                }
//                            }
//                            break;
//
//                        case WRITTEN_EVALUATION:
//                            for (ExecutionCourse executionCourse : executionCoursesToTest) {
//                                SortedSet<WrittenEvaluation> writtenEvaluations = executionCourse.getWrittenEvaluations();
//                                for (WrittenEvaluation writtenEvaluation : writtenEvaluations) {
//                                    if (writtenEvaluation.getAssociatedRooms().contains(resource)) {
//                                        toAdd = true;
//                                        break;
//                                    }
//                                }
//                                if (toAdd) {
//                                    break;
//                                }
//                            }
//                            break;
//
//                        default:
//                            break;
//                        }
//
//                        if (!toAdd) {
//                            continue;
//                        }
//                    }
//
//                    if (building != null) {
//                        Building spaceBuilding = space.getSpaceBuilding();
//                        if (spaceBuilding == null || !spaceBuilding.equals(building)) {
//                            continue;
//                        }
//                    } else if (campus != null) {
//                        Campus spaceCampus = space.getSpaceCampus();
//                        if (spaceCampus == null || !spaceCampus.equals(campus)) {
//                            continue;
//                        }
//                    }
//
//                    result.add(space);
//                }
//            }
//        }
//        return result;
//    }

//    private static Collection<Person> searchPersonsByName(SpacesSearchCriteriaType searchType, String labelToSearch) {
//        if (labelToSearch != null && !StringUtils.isEmpty(labelToSearch) && searchType.equals(SpacesSearchCriteriaType.PERSON)) {
//            return Person.findPerson(labelToSearch);
//        }
//        return Collections.EMPTY_LIST;
//    }
//
//    private static Set<ExecutionCourse> searchExecutionCoursesByName(SpacesSearchCriteriaType searchType, String[] labelWords) {
//        Set<ExecutionCourse> executionCoursesToTest = null;
//        if (labelWords != null
//                && (searchType.equals(SpacesSearchCriteriaType.EXECUTION_COURSE) || searchType
//                        .equals(SpacesSearchCriteriaType.WRITTEN_EVALUATION))) {
//            executionCoursesToTest = new HashSet<ExecutionCourse>();
//            for (ExecutionCourse executionCourse : ExecutionSemester.readActualExecutionSemester()
//                    .getAssociatedExecutionCoursesSet()) {
//                if (executionCourse.verifyNameEquality(labelWords)) {
//                    executionCoursesToTest.add(executionCourse);
//                }
//            }
//        }
//        return executionCoursesToTest;
//    }
//
//    public SpaceAttendances addAttendance(User person, String responsibleUsername) {
//        if (person == null) {
//            return null;
//        }
//        if (!canAddAttendance()) {
//            throw new SpaceDomainException("error.space.maximumAttendanceExceeded");
//        }
//        SpaceAttendances attendance = new SpaceAttendances(person.getIstUsername(), responsibleUsername, new DateTime());
//        addCurrentAttendance(attendance);
//        addPastAttendances(attendance);
//        return attendance;
//    }
//
//    public boolean canAddAttendance() {
//        return currentAttendaceCount() < getSpaceInformation().getCapacity();
//    }
//
//    public int currentAttendaceCount() {
//        int occupants = getCurrentAttendanceSet().size();
//        for (Space space : getActiveContainedSpaces()) {
//            occupants += space.currentAttendaceCount();
//        }
//        return occupants;
//    }
}
