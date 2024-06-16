/*
 * Created 2018-7-9 16:14:46
 */
package cn.com.yting.kxy.web.invitation;

import cn.com.yting.kxy.core.AlphaDigitCodeGenerator;
import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.currency.CurrencyStack;
import cn.com.yting.kxy.web.mail.MailSendingRequest;
import cn.com.yting.kxy.web.mail.MailService;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Azige
 */
@Service
@Transactional(isolation = Isolation.SERIALIZABLE)
public class InvitationService {

    @Autowired
    private MailService mailService;

    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private InviterRepository inviterRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private final AlphaDigitCodeGenerator codeGenerator = new AlphaDigitCodeGenerator(6);

    public InviterRecord createInviterRecord(long accountId, String invitationCode) {
        return createInviterRecord(accountId, invitationCode, false);
    }

    public InviterRecord createInviterRecord(long accountId, String invitationCode, boolean ignoreInvalidCode) {
        if (inviterRepository.existsById(accountId)) {
            throw InvitationException.inviterRecordExisted();
        }
        boolean invited = false;
        if (invitationCode != null && !invitationCode.isEmpty()) {
            try {
                buildRelationship(accountId, invitationCode);
                invited = true;
            } catch (InvitationException ex) {
                if (!ignoreInvalidCode) {
                    throw ex;
                }
            }
        }

        InviterRecord record = new InviterRecord();
        record.setAccountId(accountId);
        find_a_code:
        {
            for (int i = 0; i < 1000; i++) {
                String code = codeGenerator.generateCode();
                if (!inviterRepository.existsByInvitationCode(code)) {
                    record.setInvitationCode(code);
                    break find_a_code;
                }
            }
            throw KxyWebException.unknown("无法在限定次数内找到一个有效的邀请码");
        }
        record = inviterRepository.saveAndFlush(record);

        eventPublisher.publishEvent(new InviterRecordCreatedEvent(this, accountId, invited));

        return record;
    }

    private void buildRelationship(long accountId, String invitationCode) {
        InviterRecord inviterRecord = verifyInvitationCodeAndGetInviterRecord(invitationCode);
        long inviterId = inviterRecord.getAccountId();

        InvitationRecord directInvitationRecord = new InvitationRecord();
        directInvitationRecord.setAccountId(accountId);
        directInvitationRecord.setInviterId(inviterId);
        directInvitationRecord.setInviterDepth(InvitationConstants.DIRECT_INVITATION_DEPTH);
        invitationRepository.save(directInvitationRecord);

        // 搜索邀请链并记录邀请关系
        invitationRepository.findByAccountId(inviterId).stream()
                .filter(record -> record.getInviterDepth() < InvitationConstants.MAX_INVITATION_DEPTH)
                .forEach(record -> {
                    InvitationRecord transiveInvitationRecord = new InvitationRecord();
                    transiveInvitationRecord.setAccountId(accountId);
                    transiveInvitationRecord.setInviterId(record.getInviterId());
                    transiveInvitationRecord.setInviterDepth(record.getInviterDepth() + 1);
                    invitationRepository.save(transiveInvitationRecord);
                });

        invitationRepository.flush();

        int count = invitationRepository.countByInviterIdAndInviterDepth(inviterId, 1);
        if (count == InvitationConstants.GET_TITLE_INVITATION_COUNT) {
            MailSendingRequest.create()
                    .template(InvitationConstants.INVITATION_TITLE_MAIL_ID)
                    .attachment(Collections.singletonList(new CurrencyStack(InvitationConstants.INVITATION_TITLE_CURRENCY_ID, 1)))
                    .to(inviterId)
                    .commit(mailService);
        }
    }

    public InviterRecord verifyInvitationCodeAndGetInviterRecord(String invitationCode) {
        InviterRecord inviterRecord = inviterRepository.findByInvitationCode(invitationCode);
        if (inviterRecord == null) {
            throw InvitationException.invitationCodeNotValid();
        }
        if (invitationRepository.countByInviterIdAndInviterDepth(inviterRecord.getAccountId(), InvitationConstants.DIRECT_INVITATION_DEPTH) >= inviterRecord.getInvitationLimit()) {
            throw InvitationException.inviterReachLimit();
        }
        return inviterRecord;
    }

    public InviterRecord increaseInvitationLimitForGenesis(long accountId) {
        InviterRecord inviterRecord = inviterRepository.findById(accountId).get();
        inviterRecord.setInvitationLimit(Math.max(InvitationConstants.DEFAULT_INVITATION_LIMIT_FOR_GENESIS, inviterRecord.getInvitationLimit()));
        return inviterRepository.save(inviterRecord);
    }

    public boolean isInvited(long accountId) {
        return invitationRepository.existsByAccountId(accountId);
    }
}
