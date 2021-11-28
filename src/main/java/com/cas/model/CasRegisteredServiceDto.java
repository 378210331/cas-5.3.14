package com.cas.model;

import lombok.Data;
import org.apereo.cas.services.RegisteredService;

@Data
public class CasRegisteredServiceDto {

    public CasRegisteredServiceDto(){

    }

    public CasRegisteredServiceDto(RegisteredService registeredService){
        this.id = registeredService.getId();
        this.serviceId = registeredService.getServiceId();
        this.name = registeredService.getName();
        this.description = registeredService.getDescription();
        this.logoutUrl = registeredService.getLogoutUrl() == null ? "" : registeredService.getLogoutUrl().toString();
    }

    /**
     * id，自增
     */
    Long id ;

    /**
     * 服务id,一般是客户端登录的首页地址
     */
    String serviceId;

    /**
     * 服务名称
     */
    String name;

    /**
     * 登出url,cas登出后回调
     */
    String logoutUrl;

    /**
     * 描述
     */
    String description;




}
