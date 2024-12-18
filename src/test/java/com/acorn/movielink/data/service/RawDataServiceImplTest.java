package com.acorn.movielink.data.service;

import com.acorn.movielink.data.dto.RAWDataDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

@SpringBootTest
class RawDataServiceImplTest {

    @Autowired
    private RawDataService service;

    @Test
    void test(){
        List<RAWDataDTO> list = service.findDataBetween("20240101", "20240102");
        list.forEach(System.out::println);
    }

    @Test
    void test2(){
        int result = service.deleteDataBetween("20241210", "20241211");
        assertTrue(result >= 1);
    }

}