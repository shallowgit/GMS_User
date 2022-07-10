package gms.cuit.controller;

import gms.cuit.dto.PageDTO;
import gms.cuit.entity.Gms_Equipment;
import gms.cuit.entity.Gms_User;
import gms.cuit.entity.PageBean;
import gms.cuit.service.EquipmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class EquipmentController {
    @Resource
    private EquipmentService equipmentService;

    @PostMapping("/getEquipment")
    public Map<String,Object> getEquipment(@RequestBody Map<String,String> reqmap){
        Map<String,Object> map = new HashMap<>();
        //获取模糊查询
        String query_key = reqmap.get("query_key");
        if(query_key==null) query_key="";
        if(query_key!=null&&query_key.trim().equals("")==false) query_key=query_key.trim();

        //设置当前页和当前显示条数
        String currentPageStr = reqmap.get("currentPage");
        if (currentPageStr == null||currentPageStr.equals("")) currentPageStr = "1";
        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 6;

        PageBean pageBean = equipmentService.findAllEquipments(currentPage,currentCount,query_key);
        map.put("query_key", query_key);
        map.put("currentPage",pageBean.getCurrentPage());
        map.put("currentCount",pageBean.getCurrentCount());
        map.put("totalCount",pageBean.getTotalCount());
        map.put("totalPage",pageBean.getTotalPage());
        map.put("list",pageBean.getList());
        return map;
    }
}
