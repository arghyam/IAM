package org.forwater.backend.dao.Impl;

import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.forwater.backend.dao.MapMyIndiaService;
import org.forwater.backend.dto.MapMyIndiaDTO;
import org.forwater.backend.dto.MapMyIndiaLocationInfoDTO;
import org.forwater.backend.service.ServiceImpl.SearchServiceImpl;
import org.forwater.backend.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Component
@Service
public class MapMyIndiaServiceImpl implements MapMyIndiaService {


    private static Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);


    @Override
    public List<MapMyIndiaLocationInfoDTO> getAddressDetails(Double lat, Double lng) throws IOException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl httpUrl=new HttpUrl.Builder()
                .scheme("https")
                .host("apis.mapmyindia.com")
                .addPathSegment("advancedmaps")
                .addPathSegment("v1")
                .addPathSegment(Constants.MAP_MY_INDIA_LICENCE)
                .addPathSegment(Constants.MAP_MY_INDIA_CODE)
                .addQueryParameter("lat",String.valueOf(lat))
                .addQueryParameter("lng",String.valueOf(lng))
                .build();

        System.out.println(httpUrl.toString());

        Request requesthttp = new Request.Builder()
                .addHeader("accept", "application/json")
                .url(httpUrl) // <- Finally put httpUrl in here
                .build();

        Response response=client.newCall(requesthttp).execute();
        Gson gson=new Gson();
        MapMyIndiaDTO mapMyIndiaDTO= gson.fromJson(response.body().string(),MapMyIndiaDTO.class);
        System.out.println(""+mapMyIndiaDTO.getLocationInfoList());
        return mapMyIndiaDTO.getLocationInfoList();


    }
}
