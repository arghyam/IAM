package org.forwater.backend.dao;

import org.forwater.backend.dto.MapMyIndiaLocationInfoDTO;

import java.io.IOException;
import java.util.List;

public interface MapMyIndiaService {
    List<MapMyIndiaLocationInfoDTO> getAddressDetails(Double lat, Double lng) throws IOException;
}
