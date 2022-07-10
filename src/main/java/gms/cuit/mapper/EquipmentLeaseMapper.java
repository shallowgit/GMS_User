package gms.cuit.mapper;

import gms.cuit.entity.Gms_Equipment_Lease;

import java.util.List;

public interface EquipmentLeaseMapper {
/*    void addEquipmentLease(Gms_Equipment_Lease lease);

    Gms_Equipment_Lease getEquipmentLease(String lease_Id);

    List<Gms_Equipment_Lease> getEquipmentLeaseByEquipmentNameOrUserId(String equipmentName, String userId);

    List<Gms_Equipment_Lease> getAllEquipmentLeases();

    void deleteEquipmentLease(String lease_Id);

    void updateEquipmentLease(Gms_Equipment_Lease lease);

    void updateReturn(Gms_Equipment_Lease lease);

    long getCount();*/

    //获取包含指定关键字的某页订单
    List<Gms_Equipment_Lease> findProductByPage(String user_Id, int index, int currentCount, String query_key);

    //通过id修改订单状态
    void delLeaseById(String lease_Id);

    //插入订单
    int addLease(Gms_Equipment_Lease lease);

    Integer getCount(String user_Id, String query_key);
}
