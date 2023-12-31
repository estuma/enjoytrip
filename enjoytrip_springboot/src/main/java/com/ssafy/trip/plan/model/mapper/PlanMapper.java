package com.ssafy.trip.plan.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.trip.plan.model.AttractionDto;
import com.ssafy.trip.plan.model.AttractionInfoDto;
import com.ssafy.trip.plan.model.GugunDto;
import com.ssafy.trip.plan.model.PlanDto;
import com.ssafy.trip.plan.model.PlanListDetailDto;
import com.ssafy.trip.plan.model.PlanListDto;
import com.ssafy.trip.plan.model.PlanNthDetailRegistDto;
import com.ssafy.trip.plan.model.SidoGugunNameDto;
import com.ssafy.trip.plan.model.Wishlist;

@Mapper
public interface PlanMapper {
	
	// attraction
	List<AttractionDto> attractionList(Map<String, String> map);
	AttractionDto getAttraction(int contentId);
	List<GugunDto> gugun(int sidoCode);
	SidoGugunNameDto sidoGugunName(int sidoCode, int gugunCode);
	
	// plan
	void registPlan(PlanDto planDto);
	int getPlanPeriod(int planId);
	void registPlanNth(Map<String, Integer> map); // planId, 여행날짜 전달(1,2,3)
	int registPlanNthDetail(PlanNthDetailRegistDto registDto); // ??
	
	List<PlanListDetailDto> getPlan(Map<String, String> map); // int planId, String userId
	List<PlanListDto> planList(String userId); 

	int modifyPlanName(String planName, String planId);
	int modifyPlanDate(Map<String, String> map);
	Map<String, Integer> getInsertPoint(Map<String, Integer> map);	// planId, userPlanNth
	List<Integer> getInsertPlanNthDetail(int planId, int nextPeriod);
	
	int deletePlanNth(int planId, int userPlanNth);
	int deletePlanNthDetail();
	int deletePlan(int planId);
	int deletePlanNthDetail(int planId);
	
	// wishlist
	List<AttractionInfoDto> wishlist(String userId);
	int deleteWish(Wishlist wishlist);
	void deleteWishlist(String userId);
	void registWishlist(List<Wishlist> list);
	AttractionInfoDto attractionListById(int contentId);
	
	
}
