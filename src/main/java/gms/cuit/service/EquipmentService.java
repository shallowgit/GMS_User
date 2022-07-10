package gms.cuit.service;

import gms.cuit.entity.Gms_Equipment;
import gms.cuit.entity.PageBean;
import gms.cuit.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    public PageBean<Gms_Equipment> findAllEquipments(int currentPage, int currentCount, String query_key) {
        PageBean<Gms_Equipment> pageBean = new PageBean<>();
        pageBean.setCurrentPage(currentPage);
        pageBean.setCurrentCount(currentCount);
        int totalCount = equipmentMapper.getCount("", query_key);
        pageBean.setTotalCount(totalCount);
        int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
        pageBean.setTotalPage(totalPage);
        //5.封装当前页显示的数据
        int index = (currentPage - 1) * currentCount;
        //将list<map<>>遍历封装到orderlist
        List<Gms_Equipment> list = equipmentMapper.findAllEquipments(index, currentCount, query_key);
        pageBean.setList(list);
        return pageBean;
    }

    public void returnEquipment(String equipment_Id, int count) {
        equipmentMapper.returnEquipment(equipment_Id, count);
    }

    public Gms_Equipment findEquipmentById(String id) {
        return equipmentMapper.findEquipmentById(id);
    }
}
