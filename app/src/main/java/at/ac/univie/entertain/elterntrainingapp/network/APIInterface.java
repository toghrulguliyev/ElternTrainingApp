package at.ac.univie.entertain.elterntrainingapp.network;

import java.util.List;

import at.ac.univie.entertain.elterntrainingapp.Config.Const;
import at.ac.univie.entertain.elterntrainingapp.model.Consequence;
import at.ac.univie.entertain.elterntrainingapp.model.Emotions;
import at.ac.univie.entertain.elterntrainingapp.model.Event;
import at.ac.univie.entertain.elterntrainingapp.model.FamilyRule;
import at.ac.univie.entertain.elterntrainingapp.model.Gedanke;
import at.ac.univie.entertain.elterntrainingapp.model.Goals;
import at.ac.univie.entertain.elterntrainingapp.model.Loben;
import at.ac.univie.entertain.elterntrainingapp.model.Login;
import at.ac.univie.entertain.elterntrainingapp.model.MyGoals;
import at.ac.univie.entertain.elterntrainingapp.model.Response;
import at.ac.univie.entertain.elterntrainingapp.model.Selfportrait;
import at.ac.univie.entertain.elterntrainingapp.model.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("register")
    Call<Response> createUser(@Body User user);

    @POST("userlogin")
    Call<Response> login(@Body Login login);

    @POST("save_rule")
    Call<Response> saveRule(@Header(Const.TOKEN_KEY) String token, @Body FamilyRule fr);

    @FormUrlEncoded
    @POST("remove_fr")
    Call<Response> removeFr(@Header(Const.TOKEN_KEY) String token, @Field("id") String id);

    @FormUrlEncoded
    @POST("get_rules")
    Call<List<FamilyRule>> getrules(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("familyId") String familyId);

    @FormUrlEncoded
    @POST("get_user")
    Call<User> getUser(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("change_pwd")
    Call<Response> changePwd(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("newPassword") String newPassword);

    @FormUrlEncoded
    @POST("get_user_members")
    Call<List<User>> getMembers(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("detach_from_family")
    Call<Response> detachFamily(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("search_users")
    Call<List<String>> searchUsers(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("add_relative")
    Call<Response> addRelative(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("relative") String relative);

    @FormUrlEncoded
    @POST("get_quality")
    Call<Selfportrait> getQuality(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @POST("save_qualities")
    Call<Response> saveQualities(@Header(Const.TOKEN_KEY) String token, @Body Selfportrait sp);

    @FormUrlEncoded
    @POST("save_emotions")
    Call<Response> saveEmotions(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("emotions") float[] emotions);

    @FormUrlEncoded
    @POST("get_family_emotions")
    Call<List<Emotions>> getFamilyEmotions(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("get_emotions")
    Call<Emotions> getEmotions(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @POST("remove_quality")
    Call<Response> removeQuality(@Header(Const.TOKEN_KEY) String token, @Body Selfportrait sp);

    @FormUrlEncoded
    @POST("get_events")
    Call<List<Event>> getEvents(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("familyId") String familyId);

    @POST("save_event")
    Call<Response> saveEvent(@Header(Const.TOKEN_KEY) String token, @Body Event event);

    @FormUrlEncoded
    @POST("remove_event")
    Call<Response> removeEvent(@Header(Const.TOKEN_KEY) String token, @Field("id") long id);

    @FormUrlEncoded
    @POST("remove_member")
    Call<Response> removeMember(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("relative") String relative);

    @POST("get_all_goals")
    Call<Goals> getAllGoals(@Header(Const.TOKEN_KEY) String token);

    @FormUrlEncoded
    @POST("get_my_goals")
    Call<MyGoals> getMyGoals(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @POST("save_my_goals")
    Call<Response> saveMyGoals(@Header(Const.TOKEN_KEY) String token, @Body MyGoals myGoals);

    @FormUrlEncoded
    @POST("get_my_gedanke")
    Call<List<Gedanke>> getMyGedanke(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @POST("save_gedanke")
    Call<Response> saveGedanke(@Header(Const.TOKEN_KEY) String Token, @Body Gedanke gedanke);

    @FormUrlEncoded
    @POST("remove_gedanke")
    Call<Response> removeGedanke(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("id") String id);

    @POST("save_loben")
    Call<Response> saveLoben(@Header(Const.TOKEN_KEY) String token, @Body Loben loben);

    @FormUrlEncoded
    @POST("get_loben")
    Call<List<Loben>> getLoben(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("remove_loben")
    Call<Response> removeLoben(@Header(Const.TOKEN_KEY) String token, @Field("id") String id);

    @FormUrlEncoded
    @POST("get_cons")
    Call<List<Consequence>> getCons(@Header(Const.TOKEN_KEY) String token, @Field("username") String username);

    @FormUrlEncoded
    @POST("remove_cons")
    Call<Response> removeCons(@Header(Const.TOKEN_KEY) String token, @Field("id") String id);

    @POST("save_cons")
    Call<Response> saveCons(@Header(Const.TOKEN_KEY) String token, @Body Consequence cons);

    @FormUrlEncoded
    @POST("save_fcm_token")
    Call<Response> saveFcmToken(@Header(Const.TOKEN_KEY) String token, @Field("username") String username, @Field("fcmToken") String fcmToken);




}
