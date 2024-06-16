/*
 * Created 2018-12-19 12:53:36
 */
package cn.com.yting.kxy.web.game.mingjiandahui;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Scanner;

import cn.com.yting.kxy.web.account.Account;
import cn.com.yting.kxy.web.account.AccountRepository;
import cn.com.yting.kxy.web.equipment.Equipment;
import cn.com.yting.kxy.web.equipment.EquipmentService;
import cn.com.yting.kxy.web.pet.Pet;
import cn.com.yting.kxy.web.pet.PetService;
import cn.com.yting.kxy.web.player.Player;
import cn.com.yting.kxy.web.player.PlayerRelation;
import cn.com.yting.kxy.web.player.PlayerRelationRepository;
import cn.com.yting.kxy.web.player.PlayerRepository;
import cn.com.yting.kxy.web.school.SchoolRecord;
import cn.com.yting.kxy.web.school.SchoolRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 *
 * @author Azige
 */
@Component
public class MjdhDummyCreator implements InitializingBean {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private MjdhDummyRepository mjdhDummyRepository;
    @Autowired
    private PlayerRelationRepository playerRelationRepository;

    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private PetService petService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.execute(status -> {
            if (mjdhDummyRepository.count() == 0) {
                try (Scanner scanner = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("新创角色v1.1.csv"), "UTF-8")) {
                    int count = 0;
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String[] args = line.split(",");

                        String name = "dummy_" + count++;
                        Account account = new Account();
                        account.setUsername(name);
                        account.setDisplayName(name);
                        account.setCreateTime(new Date());
                        account = accountRepository.saveAndFlush(account);

                        Player player = new Player();
                        player.setAccountId(account.getId());
                        player.setPlayerName(args[0]);
                        player.setPlayerLevel(Integer.parseInt(args[1]));
                        player.setPrefabId(Integer.parseInt(args[4]));
                        player.setCreateTime(new Date());
                        player.setLastLoginTime(new Date());
                        playerRepository.save(player);

                        SchoolRecord schoolRecord = new SchoolRecord();
                        schoolRecord.setAccountId(account.getId());
                        schoolRecord.setSchoolId(Long.parseLong(args[2]));
                        schoolRecord.setAbility_1_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_2_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_3_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_4_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_5_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_6_level(Integer.parseInt(args[3]));
                        schoolRecord.setAbility_7_level(Integer.parseInt(args[3]));
                        schoolRepository.save(schoolRecord);

                        Equipment handEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[5]));
                        Equipment headEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[6]));
                        Equipment bodyEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[7]));
                        Equipment footEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[8]));
                        Equipment waistEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[9]));
                        Equipment neckEquipment = equipmentService.createAndSaveEquipmentByPrototype(account.getId(), Long.parseLong(args[10]));

                        Pet pet1 = petService.createAndSavePetByPrototype(account.getId(), Long.parseLong(args[11]));
                        Pet pet2 = petService.createAndSavePetByPrototype(account.getId(), Long.parseLong(args[12]));
                        Pet pet3 = petService.createAndSavePetByPrototype(account.getId(), Long.parseLong(args[13]));

                        PlayerRelation playerRelation = new PlayerRelation();
                        playerRelation.setAccountId(account.getId());
                        playerRelation.setHandEquipmentId(handEquipment.getId());
                        playerRelation.setHeadEquipmentId(headEquipment.getId());
                        playerRelation.setBodyEquipmentId(bodyEquipment.getId());
                        playerRelation.setFootEquipmentId(footEquipment.getId());
                        playerRelation.setWaistEquipmentId(waistEquipment.getId());
                        playerRelation.setNeckEquipmentId(neckEquipment.getId());
                        playerRelation.setBattlePetId1(pet1.getId());
                        playerRelation.setBattlePetId2(pet2.getId());
                        playerRelation.setBattlePetId3(pet3.getId());
                        playerRelationRepository.save(playerRelation);

                        MjdhDummyRecord dummyRecord = new MjdhDummyRecord();
                        dummyRecord.setAccountId(account.getId());
                        mjdhDummyRepository.save(dummyRecord);
                    }
                }
            }
            return null;
        });
    }
}
