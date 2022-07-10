package gms.cuit.controller;

import gms.cuit.entity.*;
import gms.cuit.service.EquipmentLeaseService;
import gms.cuit.service.EquipmentService;
import gms.cuit.service.UsernamePasswordUtilsService;
import gms.cuit.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class EquipmentLeaseController {

    private SnowFlake snowFlake = new SnowFlake(2,3);

    @Resource
    private EquipmentLeaseService equipmentLeaseService;

    @Resource
    private EquipmentService equipmentService;

    @Autowired
    private UsernamePasswordUtilsService usernamePasswordUtilsService;

    @PostMapping("/getLease")
    public Map<String,Object> getLease(@RequestBody Map<String,String> reqmap){
        Map<String,Object> map = new HashMap<>();
        //获取模糊查询
        String query_key = reqmap.get("query_key");
        if(query_key==null) query_key="";
        if(query_key!=null&&query_key.trim().equals("")==false) query_key=query_key.trim();
        //获取当前对象的id
        Gms_User session = usernamePasswordUtilsService.getSession();
        String user_Id = session.getUser_Id();
        //设置当前页和当前显示条数
        String currentPageStr = reqmap.get("currentPage");
        if (currentPageStr == null||currentPageStr.equals("")) currentPageStr = "1";
        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 6;

        PageBean pageBean = equipmentLeaseService.findOrderListByUserId(user_Id,currentPage,currentCount,query_key);
        map.put("query_key", query_key);
        map.put("currentPage",pageBean.getCurrentPage());
        map.put("currentCount",pageBean.getCurrentCount());
        map.put("totalCount",pageBean.getTotalCount());
        map.put("totalPage",pageBean.getTotalPage());
        map.put("list",pageBean.getList());
        return map;
    }

    @PostMapping("/delLease")
    public Map<String,String> delLease(@RequestBody Map<String,String> reqmap){
        Map<String,String> map = new HashMap<>();
        String leaseid = reqmap.get("leaseId");//租借号
        String equipmentid = reqmap.get("equipmentId");//器材id
        String equipment_Count = reqmap.get("count");//器材数量
        int count = Integer.parseInt(equipment_Count);
        equipmentLeaseService.delLeaseById(leaseid);//租借改变状态
        equipmentService.returnEquipment(equipmentid,count);
        map.put("info","ok");
        return map;
    }

    @PostMapping("/addLease")
    public Map<String, String> addLease(@RequestBody Gms_Equipment_Lease lease,@RequestParam String equipment_Id){
        Map<String,String> map = new HashMap<>();
        //封装订单号
        lease.setLease_Id(String.valueOf(snowFlake.nextId()));
        //封装场馆
        Gms_Equipment equipmentById = equipmentService.findEquipmentById(equipment_Id);
        lease.setGms_Equipment(equipmentById);
        //封装用户user
        Gms_User user = usernamePasswordUtilsService.getSession();
        //Gms_User user = userService.finUserById(order_User);
        lease.setGms_User(user);
        //封装订单状态
        lease.setLease_State(0);
        //提交订单
        boolean res = equipmentLeaseService.addLease(lease);
        if(res){
            //表示借用成功
            map.put("info","ok");
        }else{
            //表示借用失败
            map.put("info","error");
        }
        return map;
    }

    /*@RequestMapping("/equipment_admin/equipmentLease/list")
    public String List(){
        return "/pages/view/equipment_admin/equipmentLease/list";
    }

    @RequestMapping("/equipment_admin/equipmentLease/getAllEquipmentLeases")
    @ResponseBody
    public Object getAllEquipmentLeases(){
        return new PageDTO<Gms_Equipment_Lease>().toPageDTO(equipmentLeaseService.getAllEquipmentLeases());
    }

    @RequestMapping("/equipment_admin/equipmentLease/getEquipmentLeasesByEquipmentOrStudent")
    @ResponseBody
    public Object getEquipmentLeasesByEquipmentOrStudent(String equipmentName, String userId){
        return new PageDTO<Gms_Equipment_Lease>().toPageDTO(
                equipmentLeaseService.getEquipmentLeaseByEquipmentNameOrUserId(equipmentName, userId));
    }

    @RequestMapping(value = "/equipment_admin/equipment/addLease", method = RequestMethod.POST)
    @ResponseBody
    public String addLease(EquipmentLeaseDTO equipmentLeaseDTO) {
        equipmentLeaseService.addEquipmentLease(equipmentLeaseDTO);
        return "success";
    }

    @RequestMapping(value = "/equipment_admin/equipmentLease/returnEquipment", method = RequestMethod.POST)
    @ResponseBody
    public String returnEquipment(String equipmentLeaseId) {
        equipmentLeaseService.returnEquipment(equipmentLeaseId);
        return "true";
    }

    @RequestMapping(value = "/equipment_admin/equipmentLease/edit", method = RequestMethod.POST)
    @ResponseBody
    public String edit(Gms_Equipment_Lease equipmentLease) {
        equipmentLeaseService.updateEquipmentLease(equipmentLease);
        return "success";
    }

    @RequestMapping(value = "/equipment_admin/equipmentLease/delete", method = RequestMethod.POST)
    @ResponseBody
    public String delete(@RequestParam String id) {
        equipmentLeaseService.deleteEquipmentLease(id);
        return "true";
    }*/
}
