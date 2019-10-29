package postpc.studypartner2.repository.notifications;

import postpc.studypartner2.model.notifications.Response;
import postpc.studypartner2.model.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA8nKFCCY:APA91bEgDiZ_VPZJB_QdzhChKXurHwnuoQl_64wmtlpzuSZS1etE1L6WrWy4-oTcxBRF-VAn-V_hUIHmLfVqIDeM1iuSeZ5NXjRcbjSVPu7Osh-VLXBlRfOPFbCfDeLZd8N8Eoy6zq7K"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
