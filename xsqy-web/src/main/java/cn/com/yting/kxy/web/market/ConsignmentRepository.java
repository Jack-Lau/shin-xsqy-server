/*
 * Created 2018-12-19 16:54:36
 */
package cn.com.yting.kxy.web.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import cn.com.yting.kxy.core.util.CommaSeparatedLists;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Azige
 */
public interface ConsignmentRepository extends JpaRepository<Consignment, Long>, JpaSpecificationExecutor<Consignment> {

    @Query("SELECT r FROM Consignment r WHERE r.id = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Consignment> findByIdForWrite(long id);

    @Query("SELECT COUNT(r) FROM Consignment r WHERE r.sellerAccountId = ?1 AND r.dealTime = null AND r.deadline > ?2")
    long countOnSaleBySellerAccountId(long sellerAccountId, Date currentTime);

    @Query("SELECT r FROM Consignment r WHERE r.sellerAccountId = ?1")
    List<Consignment> findBySellerAccountId(long sellerAccountId);

    @Query("SELECT r FROM Consignment r WHERE r.buyerAccountId = ?1")
    List<Consignment> findByBuyerAccountId(long buyerAccountId);

    @Query("SELECT r FROM Consignment r"
        + " JOIN r.markers marker"
        + " LEFT JOIN r.markers myMarker ON myMarker.accountId = :accountId"
        + " WHERE r.deadline > :currentTime AND r.sold IS FALSE"
        + " GROUP BY r"
        + " ORDER BY COUNT(myMarker) DESC, COUNT(marker) DESC, r.id"
    )
    Page<Consignment> findByOnSaleOrdered(@Param("currentTime") Date currentTime, @Param("accountId") long accountId, Pageable pageable);

    @Query("SELECT r FROM Consignment r"
        + " LEFT JOIN r.markers marker"
        + " LEFT JOIN r.markers myMarker ON myMarker.accountId = :accountId"
        + " WHERE r.deadline > :currentTime AND r.sold IS FALSE AND r.goodsType = :goodsType"
        + " GROUP BY r"
        + " ORDER BY COUNT(myMarker) DESC, COUNT(marker) DESC, r.id"
    )
    Page<Consignment> findByGoodsTypeOnSaleOrdered(@Param("currentTime") Date currentTime, @Param("accountId") long accountId, @Param("goodsType") GoodsType goodsType, Pageable pageable);

    @RequiredArgsConstructor
    abstract class FilterByGoodsTypeOrderedSpecification implements Specification<Consignment> {

        private final long accountId;
        private final Date currentTime;
        private final GoodsType goodsType;

        @Override
        public Predicate toPredicate(Root<Consignment> consignment, CriteriaQuery<?> query, CriteriaBuilder cb) {
            ListJoin<Consignment, ConsignmentMarker> marker = consignment.joinList("markers", JoinType.LEFT);
            query.groupBy(consignment);
            query.orderBy(
                cb.desc(cb.coalesce(cb.sum(cb.<Integer>selectCase().when(cb.equal(marker.get("accountId"), accountId), 1).otherwise(0)), 0)),
                cb.desc(cb.count(marker)),
                cb.asc(consignment.get("id"))
            );

            Predicate predicate = cb.and(
                cb.equal(consignment.get("goodsType"), goodsType),
                cb.equal(consignment.get("sold"), false),
                cb.greaterThan(consignment.<Date>get("deadline"), currentTime)
            );

            return cb.and(predicate, createQueryPredicate(consignment, query, cb));
        }

        protected abstract Predicate createQueryPredicate(Root<Consignment> consignment, CriteriaQuery<?> query, CriteriaBuilder cb);
    }

    default Page<Consignment> findEquipmentConsignments(long accountId, Date currentTime, EquipmentQueryParameter param, Pageable pageable) {
        return findAll(new FilterByGoodsTypeOrderedSpecification(accountId, currentTime, GoodsType.EQUIPMENT) {
            @Override
            protected Predicate createQueryPredicate(Root<Consignment> consignment, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<Consignment, ConsignmentEquipmentInfo> equipmentInfo = consignment.join("consignmentEquipmentInfo");

                List<Predicate> topLevelPredicates = new ArrayList<>();

                if (param.getPart() != null) {
                    topLevelPredicates.add(cb.equal(equipmentInfo.get("part"), param.getPart()));
                }
                if (param.getColor() != null) {
                    topLevelPredicates.add(cb.equal(equipmentInfo.get("color"), param.getColor()));
                }
                if (param.getMaxEnhanceLevel() != null) {
                    topLevelPredicates.add(cb.ge(equipmentInfo.get("maxEnhanceLevel"), param.getMaxEnhanceLevel()));
                }

                {
                    List<Predicate> predicates = new ArrayList<>();
                    if (param.getPatk() != null) {
                        predicates.add(cb.ge(equipmentInfo.get("patk"), param.getPatk()));
                    }
                    if (param.getMatk() != null) {
                        predicates.add(cb.ge(equipmentInfo.get("matk"), param.getMatk()));
                    }
                    if (param.getFc() != null) {
                        predicates.add(cb.ge(equipmentInfo.get("fc"), param.getFc()));
                    }

                    if (param.getParamMatch().equals("all")) {
                        topLevelPredicates.add(cb.and(predicates.stream().toArray(Predicate[]::new)));
                    } else if (param.getParamMatch().equals("any")){
                        topLevelPredicates.add(cb.or(predicates.stream().toArray(Predicate[]::new)));
                    }
                }

                if (param.getEffectIdsText() != null && !param.getEffectIdsText().isEmpty()) {
                    List<Long> effectIds = CommaSeparatedLists.fromText(param.getEffectIdsText(), Long::valueOf);
                    if (param.getEffectMatch().equals("all")) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<ConsignmentEquipmentEffect> e = subquery.from(ConsignmentEquipmentEffect.class);
                        subquery.select(e.get("effectId")).where(cb.equal(e.get("consignment"), consignment));
                        Predicate[] predicates = effectIds.stream()
                            .map(id -> cb.literal(id).in(subquery))
                            .toArray(Predicate[]::new);
                        topLevelPredicates.add(cb.and(predicates));
                    } else if (param.getEffectMatch().equals("any")) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<ConsignmentEquipmentEffect> e = subquery.from(ConsignmentEquipmentEffect.class);
                        subquery.select(cb.literal(1L)).where(cb.and(cb.equal(e.get("consignment"), consignment), e.get("effectId").in(effectIds)));
                        topLevelPredicates.add(cb.exists(subquery));
                    }
                }

                if (param.getSkillEnhancementEffectIds() != null && !param.getSkillEnhancementEffectIds().isEmpty()) {
                    List<Long> effectIds = CommaSeparatedLists.fromText(param.getSkillEnhancementEffectIds(), Long::valueOf);
                    Subquery<Long> subquery = query.subquery(Long.class);
                    Root<ConsignmentEquipmentEffect> e = subquery.from(ConsignmentEquipmentEffect.class);
                    subquery.select(cb.literal(1L)).where(cb.and(cb.equal(e.get("consignment"), consignment), e.get("effectId").in(effectIds)));
                    topLevelPredicates.add(cb.exists(subquery));
                }

                return cb.and(topLevelPredicates.stream().toArray(Predicate[]::new));
            }
        }, pageable);
    }

    default Page<Consignment> findPetConsignments(long accountId, Date currentTime, PetQueryParameter param, Pageable pageable) {
        return findAll(new FilterByGoodsTypeOrderedSpecification(accountId, currentTime, GoodsType.PET) {
            @Override
            protected Predicate createQueryPredicate(Root<Consignment> consignment, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Join<Consignment, ConsignmentPetInfo> petInfo = consignment.join("consignmentPetInfo");

                List<Predicate> topLevelPredicates = new ArrayList<>();
                if (param.getPetDefinitionId() != null) {
                    topLevelPredicates.add(cb.equal(consignment.get("goodsDefinitionId"), param.getPetDefinitionId()));
                }

                if (param.getPetRank() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("petRank"), param.getPetRank()));
                }
                if (param.getMaxPetRank() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("maxPetRank"), param.getMaxPetRank()));
                }

                if (param.getAptitudeAtk() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("aptitudeAtk"), param.getAptitudeAtk()));
                }
                if (param.getAptitudeHp() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("aptitudeHp"), param.getAptitudeHp()));
                }
                if (param.getAptitudeMdef() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("aptitudeMdef"), param.getAptitudeMdef()));
                }
                if (param.getAptitudePdef() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("aptitudePdef"), param.getAptitudePdef()));
                }
                if (param.getAptitudeSpd() != null) {
                    topLevelPredicates.add(cb.ge(petInfo.get("aptitudeSpd"), param.getAptitudeSpd()));
                }

                if (param.getAbilityIdsText() != null && !param.getAbilityIdsText().isEmpty()) {
                    List<Long> abilityIds = CommaSeparatedLists.fromText(param.getAbilityIdsText(), Long::valueOf);
                    if (param.getAbilityMatch().equals("all")) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<ConsignmentPetAbility> a = subquery.from(ConsignmentPetAbility.class);
                        subquery.select(a.get("abilityId")).where(cb.equal(a.get("consignment"), consignment));
                        Predicate[] predicates = abilityIds.stream()
                            .map(id -> cb.literal(id).in(subquery))
                            .toArray(Predicate[]::new);
                        topLevelPredicates.add(cb.and(predicates));
                    } else if (param.getAbilityMatch().equals("any")) {
                        Subquery<Long> subquery = query.subquery(Long.class);
                        Root<ConsignmentPetAbility> a = subquery.from(ConsignmentPetAbility.class);
                        subquery.select(cb.literal(1L)).where(cb.and(cb.equal(a.get("consignment"), consignment), a.get("abilityId").in(abilityIds)));
                        topLevelPredicates.add(cb.exists(subquery));
                    }
                }

                return cb.and(topLevelPredicates.stream().toArray(Predicate[]::new));
            }
        }, pageable);
    }

    @Query("SELECT c FROM Consignment c JOIN c.markers m WHERE m.accountId = ?1")
    List<Consignment> findByMarker(long accountId);

    long countBySellerAccountIdAndSoldIsTrue(long sellerAccountId);

    long countBySellerAccountIdAndGoodsType(long sellerAccountId, GoodsType goodsType);

    long countByBuyerAccountIdAndGoodsType(long buyerAccountId, GoodsType goodsType);
}
