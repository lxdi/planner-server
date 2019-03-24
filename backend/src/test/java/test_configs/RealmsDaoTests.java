package test_configs;

import model.dao.IRealmDAO;
import model.entities.Realm;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.TestCase.assertTrue;

public class RealmsDaoTests extends AbstractTestsWithTargets {

    @Autowired
    IRealmDAO realmDAO;

    @Test
    public void setCurrentTest(){
        Realm realm1 = new Realm("realm test 1");
        realmDAO.saveOrUpdate(realm1);

        Realm realm2 = new Realm("realm test 2");
        realmDAO.saveOrUpdate(realm2);

        Realm realm3 = new Realm("realm test 3");
        realmDAO.saveOrUpdate(realm3);

        realmDAO.setCurrent(realm2.getId());

        assertTrue(!realmDAO.realmById(realm1.getId()).isCurrent());
        assertTrue(realmDAO.realmById(realm2.getId()).isCurrent());
        assertTrue(!realmDAO.realmById(realm3.getId()).isCurrent());

    }
}
