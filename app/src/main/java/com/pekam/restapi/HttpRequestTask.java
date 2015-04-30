package com.pekam.restapi;

import com.pekam.entities.TblGps;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HttpRequestTask extends AsyncTask<ArrayList<?>,Integer,ArrayList<?> > {

    final String urlGET = "http://10.0.2.2:8080/TblGps/get?id=1";
    final String urlGETList = "http://10.0.2.2:8080/TblGps/getList/";
    final String urlPOST = "http://10.0.2.2:8080/TblGps/update/";
    final String urlPOSTList = "http://10.0.2.2:8080/TblGps/updateList/";
    private RestTemplate restTemplate ;
    private ArrayList objectResult = new ArrayList();

    @Override
    protected void onProgressUpdate(Integer... values) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(50);
    }



    @Override
    protected ArrayList<?> doInBackground(ArrayList<?>... params) {
        try {

            if (params[0].size() == 1) {
                Object result = restTemplate.postForObject(urlPOST, params[0].get(0), TblGps.class);
                objectResult.add(result);
            }

            if (params[0].size() > 1) {
                objectResult = restTemplate.postForObject(urlPOSTList, params[0], ArrayList.class);
            }

        } catch (Exception e) {
            Log.e("Async Task Exception", e.getMessage(), e);
        }
        return objectResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        restTemplate= new RestTemplate(true);
        ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
        List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
        ris.add(ri);
        restTemplate.setInterceptors(ris);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    }
}



//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Accept", "application/json");
//            headers.add("Content-Type", "application/json");
//            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//            HttpEntity ent = new HttpEntity(gps,headers);
//
//            // alter the Object Data
//           gps4.setDescr("success");
//

//
//    protected ArrayList<TblGps> doInBackground(ArrayList<TblGps>... gps) {
//
//
//        ArrayList<TblGps> gpsreult = restTemplate.postForObject(urlPOSTList,gps ,ArrayList.class);
//    return gpsreult;
//    }

//    @Override
//    protected  ArrayList<TblGps> doInBackground( ArrayList<TblGps>... gps) {
//
//
//       finally {
//            this.publishProgress(100);
//
//        }
//
//    }

//       //get one Object ByID from Service
//   TblGps gps4 = (TblGps) restTemplate.getForObject(urlGET, TblGps.class);
//

//           // Post a String
// String s= restTemplate.postForObject(urlPOST2,"success", String.class);
//
//         Get a List Objects from Service
//            ResponseEntity<TblGps[]> responseEntity = restTemplate.getForEntity(urlGETList, TblGps[].class);
//            TblGps[] a = responseEntity.getBody();
//            TblGps gpsResult = a[1];
//        for (int i = 1; i <= a.length; i++) {
//            adapter.add(a[i].toString());
//             ar.add(a[i]);
//        }



//           ResponseEntity<TblGps> out  = restTemplate.postForEntity(urlPOST, HttpMethod.POST, TblGps.class,ent);
//this.publishProgress(1);



//     result2=restTemplate.postForObject(uripost,gps ,TblGps.class);

