package gms.cuit.service;

import gms.cuit.entity.Gms_Equipment_Lease;
import gms.cuit.entity.Gms_Order;
import gms.cuit.entity.PageBean;
import gms.cuit.mapper.EquipmentLeaseMapper;
import gms.cuit.mapper.EquipmentMapper;
import gms.cuit.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EquipmentLeaseService {
    @Resource
    private EquipmentLeaseMapper equipmentLeaseMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    public PageBean findOrderListByUserId(String user_Id, int currentPage, int currentCount, String query_key) {
        PageBean<Gms_Equipment_Lease> pageBean = new PageBean<>();
        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        int totalCount = equipmentLeaseMapper.getCount(user_Id, query_key);
        pageBean.setTotalCount(totalCount);
        int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
        pageBean.setTotalPage(totalPage);
        //5.封装当前页显示的数据
        int index = (currentPage - 1) * currentCount;
        //将list<map<>>遍历封装到orderlist
        List<Gms_Equipment_Lease> list = equipmentLeaseMapper.findProductByPage(user_Id, index, currentCount, query_key);
        pageBean.setList(list);
        return pageBean;
    }

    public void delLeaseById(String lease_Id) {
        equipmentLeaseMapper.delLeaseById(lease_Id);
    }

    public boolean addLease(Gms_Equipment_Lease lease){
        int res = equipmentLeaseMapper.addLease(lease);
        equipmentMapper.lendEquipment(lease.getGms_Equipment().getEquipment_Id(),lease.getLease_Count());
        if(res>0){
            return true;
        }else {
            return false;
        }
    }

/*    public void addEquipmentLease(EquipmentLeaseDTO equipmentLeaseDTO) {
        Gms_Equipment equipment = equipmentMapper.getEquipment(equipmentLeaseDTO.getEquipmentId());
        if (equipment.getEquipment_Count() - equipment.getEquipment_Lend() < equipmentLeaseDTO.getNumber()) {
            throw new RuntimeException("器材数量不足");
        } else {
            Long time = System.currentTimeMillis();
            Gms_Equipment_Lease equipmentLease = new Gms_Equipment_Lease().setLease_BorrowTime(time)
                    .setLease_State(0)
                    .setLease_Count(equipmentLeaseDTO.getNumber())
                    .setEquipment_Id(equipmentLeaseDTO.getEquipmentId())
                    .setUser_Id(userMapper.finUserById(equipmentLeaseDTO.getUserId()).getUser_Id());
            equipment.setEquipment_Lend(equipment.getEquipment_Lend() + equipmentLeaseDTO.getNumber());
            equipmentLeaseMapper.addEquipmentLease(equipmentLease);
            equipmentMapper.updateEquipmentLend(equipment);
        }
    }

    public void returnEquipment(String equipmentLeaseId) {
        Gms_Equipment_Lease equipmentLease = equipmentLeaseMapper.getEquipmentLease(equipmentLeaseId)
                .setLease_ReturnTime(System.currentTimeMillis())
                .setLease_State(1);
        Gms_Equipment equipment = equipmentMapper.getEquipment(equipmentLease.getEquipment_Id());
        equipmentMapper.updateEquipmentLend(equipment.setEquipment_Lend(equipment.getEquipment_Lend() - equipmentLease.getLease_Count()));
        equipmentLeaseMapper.updateReturn(equipmentLease);
    }

    public Gms_Equipment_Lease getEquipmentLease(String lease_Id) {
        return equipmentLeaseMapper.getEquipmentLease(lease_Id);
    }

    public List<Gms_Equipment_Lease> getEquipmentLeaseByEquipmentNameOrUserId(String equipmentName, String userId) {
        return equipmentLeaseMapper.getEquipmentLeaseByEquipmentNameOrUserId(equipmentName, userId);
    }

    public List<Gms_Equipment_Lease> getAllEquipmentLeases() {
        return equipmentLeaseMapper.getAllEquipmentLeases();
    }

    public void deleteEquipmentLease(String lease_Id) {
        equipmentLeaseMapper.deleteEquipmentLease(lease_Id);
    }

    public void updateEquipmentLease(Gms_Equipment_Lease equipmentLease) {
        equipmentLeaseMapper.updateEquipmentLease(equipmentLease);
    }

    public long getCount() {
        return equipmentLeaseMapper.getCount();
    }*/
}
