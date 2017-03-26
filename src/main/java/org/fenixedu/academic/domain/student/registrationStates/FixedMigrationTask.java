package org.fenixedu.academic.domain.student.registrationStates;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.ConnectionManager;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.bennu.scheduler.custom.CustomTask;

import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class FixedMigrationTask extends CustomTask {

    @Override
    public void runTask() throws Exception {
        int classId = findNextClassId(RegistrationStateType.class);
        StringBuilder builder = new StringBuilder();
        long oid = ((long) classId << 32);
        Map<RegistrationStateType, Long> oidMap = new HashMap<>();
        for (RegistrationStateType type : RegistrationStateType.values()) {
            oidMap.put(type, ++oid);
            out(builder,
                    "INSERT INTO REGISTRATION_STATE_TYPE (" +
                            "TERMINAL, " +
                            "OID_PROGRAM_CONCLUSION, " +
                            "ACTIVE, " +
                            "OID_INITIAL_ROOT, " +
                            "OID_INTERNAL_ABANDON_ROOT, " +
                            "MOBILITY, " +
                            "OID_EXTERNAL_ABANDON_ROOT, " +
                            "OID_FLUNKED_ROOT, " +
                            "OID, " +
                            "VALID_SOURCE, " +
                            "NAME, " +
                            "STUDENT, " +
                            "CODE, " +
                            "OID_TRANSITED_ROOT, " +
                            "OID_TRANSITION_ROOT, " +
                            "DESCRIPTION, " +
                            "OID_INTERRUPTED_ROOT, " +
                            "OID_DOMAIN_META_OBJECT, " +
                            "OID_CONCLUDED_ROOT, " +
                            "OID_CANCELED_ROOT, " +
                            "OID_DEFAULT_NEXT_STATE_TYPE, " +
                            "OID_REGISTRATION_STATE_SYSTEM, " +
                            "SCHOOL_PART_CONCLUDED) " +
                            "VALUES (%s, %s, %s, %s, %s, ); -- %s\n");
//            out(builder,
//                    "INSERT INTO REGISTRATION_STATE_TYPE (DEA, CYCLES_TO_ENROL, MASTER_DEGREE, CYCLES, OID_BENNU, BOLONHA, DEGREE_TYPE, OID, EMPTY, DFA, NAME) "
//                            + "VALUES (%s, '%s', %s, '%s', %s, %s, %s, %s, %s, %s, '%s'); -- %s\n",
//                    is(type == DegreeType.BOLONHA_ADVANCED_SPECIALIZATION_DIPLOMA),
//                    toJson(type.getSupportedCyclesToEnrol()),
//                    is(type.isMasterDegree()),
//                    toJson(type.getCycleTypes()),
//                    Bennu.getInstance().getExternalId(),
//                    is(type.isBolonhaType()),
//                    is(type.isDegree()),
//                    oidMap.get(type),
//                    is(type == DegreeType.EMPTY),
//                    is(type == DegreeType.BOLONHA_ADVANCED_FORMATION_DIPLOMA),
//                    BundleUtil.getLocalizedString(Bundle.ENUMERATION, type.name()).json().toString(),
//                    type.name());

        }

//        for (Degree degree : Bennu.getInstance().getDegreesSet()) {
//            out(builder, "UPDATE ACADEMIC_PROGRAM SET OID_DEGREE_TYPE = %s, GRADE_SCALE = %s WHERE OID = %s;\n",
//                    oidMap.get(degree.getDegreeType()),
//                    Optional.ofNullable(degree.getGradeScaleChain()).map(scale -> "'" + scale.name() + "'").orElse("NULL"),
//                    degree.getExternalId());
//        }
//
//        for (Entry<DegreeType, Long> entry : oidMap.entrySet()) {
//            out(builder, "UPDATE CURRICULAR_RULE SET OID_BOLONHA_DEGREE_TYPE = %s WHERE BOLONHA_DEGREE_TYPE = '%s';\n",
//                    entry.getValue(), entry.getKey());
//            out(builder, "UPDATE QUEUE_JOB SET OID_DEGREE_TYPE = %s WHERE DEGREE_TYPE = '%s';\n", entry.getValue(),
//                    entry.getKey());
//            out(builder, "UPDATE PERSISTENT_GROUP SET OID_DEGREE_TYPE = %s WHERE DEGREE_TYPE = '%s';\n", entry.getValue(),
//                    entry.getKey());
//        }

        output("registrationStateTypes.sql", builder.toString().getBytes());
        taskLog(builder.toString());
    }

    private static void out(StringBuilder builder, String format, Object... args) {
        builder.append(String.format(format, args));
    }

    private int findNextClassId(Class<RegistrationStateType> type) throws SQLException {
        int classId;
        try (Statement stmt = ConnectionManager.getCurrentSQLConnection().createStatement()) {
            try (ResultSet rs = stmt.executeQuery("select MAX(DOMAIN_CLASS_ID) from FF$DOMAIN_CLASS_INFO")) {
                rs.next();
                classId = rs.getInt(1) + 1;
            }
        }
        try (PreparedStatement stmt =
                     ConnectionManager.getCurrentSQLConnection().prepareStatement(
                             "INSERT INTO FF$DOMAIN_CLASS_INFO (DOMAIN_CLASS_ID, DOMAIN_CLASS_NAME) VALUES (?,?)")) {
            stmt.setInt(1, classId);
            stmt.setString(2, type.getName());
            stmt.executeUpdate();
        }
        return classId;
    }

    private static final int is(boolean bool) {
        return bool ? 1 : 0;
    }

    private static String toJson(Collection<RegistrationStateType> types) {
        return types.stream().map(RegistrationStateType::name).map(JsonPrimitive::new).collect(toJsonArray()).toString();
    }

    public static <T extends JsonElement> Collector<T, JsonArray, JsonArray> toJsonArray() {
        return Collector.of(JsonArray::new, (array, element) -> array.add(element), (one, other) -> {
            one.addAll(other);
            return one;
        }, Characteristics.IDENTITY_FINISH);
    }

    @Override
    public TxMode getTxMode() {
        return TxMode.READ;
    }

}