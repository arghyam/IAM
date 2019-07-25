package org.forwater.backend.dao;

import org.forwater.backend.dto.MapMyIndiaDTO;
import org.forwater.backend.utils.Constants;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.http.*;

@Component
public interface MapMyIndiaDAO {
    @POST(Constants.REVERSE_GEO_CODING_API)
    Call<MapMyIndiaDTO> getLocationDetails(@Path("licenceKey") String licenceKey, @Path("rev_geocode") String code,
                                           @Query("lat") String lat, @Query("lng") String lng);
}
