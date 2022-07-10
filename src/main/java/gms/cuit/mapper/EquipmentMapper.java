package gms.cuit.mapper;

import gms.cuit.entity.Gms_Equipment;
import gms.cuit.entity.Gms_Venue;

import java.util.List;

public interface EquipmentMapper {
    //添加器材
    void addEquipment(Gms_Equipment equipment);

    //获取器材信息
    //Gms_Equipment getEquipment(String equipment_Id);

    //获取包含指定关键字的某页订单
    List<Gms_Equipment> findProductByPage(String equipmentName, int index, int currentCount, String query_key);

    //获取所有器材
    List<Gms_Equipment> findAllEquipments(int index, int currentCount, String query_key);

    //删除器材
    void deleteEquipment(String equipment_Id);

    //设置器材所有信息
    void updateEquipment(Gms_Equipment equipment);

    //设置器材借出数量
    void updateEquipmentLend(Gms_Equipment equipment);

    //获取器材数量
    Integer getCount(String equipmentName, String query_key);

    //归还器材
    void returnEquipment(String equipment_Id, int count);

    //借用器材
    void lendEquipment(String equipment_Id, int count);

    //根据器材id获取器材信息
    Gms_Equipment findEquipmentById(String id);
}
