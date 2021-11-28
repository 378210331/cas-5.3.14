package com.cas.controller;

import com.cas.model.CasRegisteredServiceDto;
import com.cas.model.Result;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.RegexRegisteredService;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.ReturnAllAttributeReleasePolicy;
import org.apereo.cas.services.ServicesManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController("ServiceMangerController")
@EnableConfigurationProperties({CasConfigurationProperties.class})
@RequestMapping("/services")
public class ServiceMangerController {


    private final ServicesManager servicesManager;
    private final CasConfigurationProperties casProperties;

    public ServiceMangerController(ServicesManager servicesManager,CasConfigurationProperties casProperties){
            this.servicesManager = servicesManager;
            this.casProperties = casProperties;
    }

    @GetMapping("/all")
    public Result<List<CasRegisteredServiceDto>> getAll(){
        if(checkAuth()) {
            Collection<RegisteredService> services = servicesManager.getAllServices();
            List<CasRegisteredServiceDto> dtos = new ArrayList<>();
            for (RegisteredService service : services) {
                dtos.add(new CasRegisteredServiceDto(service));
            }
            return Result.buildSuccess("查询成功",dtos);
        }else{
            return Result.buildError("无权限");
        }
    }

    @GetMapping
    public Result<List<CasRegisteredServiceDto>> getByKeyWord(String keyWord){
        if(checkAuth()){
            List<CasRegisteredServiceDto> res = new ArrayList<>();
            if(StringUtils.isBlank(keyWord)){
                return Result.buildSuccess("查询成功",res);
            }
            Collection<RegisteredService> registeredService = servicesManager.getAllServices();
            if(registeredService !=null && registeredService.size() > 0) {
                List<RegisteredService> filter = registeredService.stream().filter(s -> StringUtils.contains(s.getName(),keyWord) || StringUtils.contains(s.getDescription(),keyWord) || StringUtils.contains(s.getServiceId(),keyWord)).collect(Collectors.toList());
                filter.forEach(s ->res.add(new CasRegisteredServiceDto(s)));
            }
            return Result.buildSuccess("查询成功",res);
        }else{
            return Result.buildError("无权限");
        }
    }

    @GetMapping("/{id}")
    public Result<CasRegisteredServiceDto> getById(@PathVariable("id") Long id){
        if(checkAuth()){
            RegisteredService registeredService = servicesManager.findServiceBy(id);
            CasRegisteredServiceDto dto = null;
            if(registeredService !=null) {
                dto = new CasRegisteredServiceDto(registeredService);
            }
            return Result.buildSuccess("查询成功",dto);
        }else{
            return Result.buildError("无权限");
        }
    }

    @PostMapping
    public Result<CasRegisteredServiceDto> add(@RequestBody CasRegisteredServiceDto dto) throws MalformedURLException {
        if(checkAuth()) {
            RegexRegisteredService service = new RegexRegisteredService();
            ReturnAllAttributeReleasePolicy re = new ReturnAllAttributeReleasePolicy();
            service.setServiceId(dto.getServiceId());
            service.setAttributeReleasePolicy(re);
            service.setName(dto.getName());
            if(StringUtils.isNotBlank(dto.getLogoutUrl())){
                service.setLogoutUrl(new URL(dto.getLogoutUrl()));//这个是为了单点登出而作用的
            }
            servicesManager.save(service);
            servicesManager.load();
            return Result.buildSuccess("新增成功", dto);
        }else {
            return Result.buildError("无权限");
        }
    }

    @DeleteMapping("/{id}")
    public Result<CasRegisteredServiceDto> deleteById(@PathVariable("id")Long id){
        if(checkAuth()) {
            servicesManager.delete(id);
            servicesManager.load();
            return Result.buildSuccess("删除成功");
        }else {
            return Result.buildError("无权限");
        }
    }


    private boolean checkAuth(){
        return StringUtils.equals(getHttpServletRequest().getHeader("attributeName"),casProperties.getRest().getAttributeName()) && StringUtils.equals(getHttpServletRequest().getHeader("attributeValue"),casProperties.getRest().getAttributeValue());
    }

    public  HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
