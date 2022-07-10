package gms.cuit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(chain = true)
public class EquipmentLeaseDTO {
    private String userId;
    private String equipmentId;
    private int number;
}
