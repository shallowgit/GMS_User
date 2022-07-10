package gms.cuit.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Gms_Equipment_Lease {

    /**
     * 租借编号
     */
    private String lease_Id;

    /**
     * 工号/学号
     */
    private Gms_User gms_User;

    /**
     * 器材编号
     */
    private Gms_Equipment gms_Equipment;

    /**
     * 借用数量
     */
    private Integer lease_Count;

    /**
     * 借用状态
     */
    private int lease_State;// 0表示未归还，1表示已归还

    /**
     * 器材借用开始日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08")
    private Date lease_BorrowTime;

    /**
     * 器材借用归还日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+08")
    private Date lease_ReturnTime;

}
