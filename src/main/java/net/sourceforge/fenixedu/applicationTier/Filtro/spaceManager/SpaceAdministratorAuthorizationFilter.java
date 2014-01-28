package net.sourceforge.fenixedu.applicationTier.Filtro.spaceManager;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NotAuthorizedException;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.space.AllocatableSpace;
import net.sourceforge.fenixedu.domain.space.Space;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;

public class SpaceAdministratorAuthorizationFilter {

    public static final SpaceAdministratorAuthorizationFilter instance = new SpaceAdministratorAuthorizationFilter();

    public void execute(AllocatableSpace fromRoom, AllocatableSpace destinationRoom) throws NotAuthorizedException {
        User userView = Authenticate.getUser();
        if (userView != null) {
            Person person = userView.getPerson();
            if (!Space.personIsSpacesAdministrator(person.getUser())) {
                throw new NotAuthorizedException();
            }
        } else {
            throw new NotAuthorizedException();
        }
    }

}
