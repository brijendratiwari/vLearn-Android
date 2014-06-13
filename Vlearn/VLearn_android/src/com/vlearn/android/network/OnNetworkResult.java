package com.vlearn.android.network;

public interface OnNetworkResult {
	//HomeACtivitynetworkCalling int function
	int MyCommunityCalloing = 1001;
	int SettingVideoCalling  = 1002;
	int uploadPhotoSetting  = 1003;
	int saveProfile  = 1004;
	int loadHashTag  = 1005;
	int loadHashVideo  = 1006;
	int loadGradeVideo  = 1007;
	int submitFeedBack  = 1008;
	int loadFeedBack = 1009;
	int loadFlag = 1010;
	int submitFlag = 1011;
	int SchoolTeacherClasses = 1012;
	int delkid = 1013;
	int loadCareerPicker = 1014;
	int addnewKidParent = 1015;
	int loadAboutYou = 1016;
	int loadDomain = 1017;
	int loadStandSkill = 1018;
	int LoadPenduingVideo = 1019;
	int uploadVideo = 1020;
	int uploadVideoThumbnail = 1021;
	int uploadCareerVideoDetails = 1022;
	int uploadCurriculumVideoDetails = 1023;
	int upCategoryStatus = 1024;
	int loadKidLeaderboard = 1025;
	int updateStatusApprove = 1026;
	int editKidDetails = 1027;
	int submitK9idPhoto = 1028;
	int loadTeachers = 1029;
	int loadParents = 1030;
	int assignKid = 1031;
	int forgetpasswordapi = 1032;
	int loadMyVideo = 1033;
	int editVideo = 1034;
	int deleteVideo = 1035;
	int loadAssignments = 1036;
	int getAvailablity = 1037;
	int getLanguageFromServer = 1038;
	void onResult(String result, int callingId);
	void onError(String result, int callingId);
}
