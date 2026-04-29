package com.soutenance.jury.dto;

import com.soutenance.jury.entity.enums.RoleJury;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class JuryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String enseignantId;
        private String soutenanceId;
        private RoleJury role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String enseignantId;
        private String soutenanceId;
        private RoleJury role;
    }
}
