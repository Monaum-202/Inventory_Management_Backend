package com.monaum.Rapid_Global.module.master.transectionCategory;

import com.monaum.Rapid_Global.enums.TransactionType;
import lombok.Data;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 *
 * @since 14-Nov-25 10:24 AM
 */

@Data
public class TransectionCategoryReqDto {

    private String name;
    private String description;
    private TransactionType type;
    private Integer sqn;
}
