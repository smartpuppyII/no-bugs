package com.meession.etm.module.crm.controller.admin.quotation;

import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageParam;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.module.crm.dal.dataobject.quotation.CrmQuotationDO;
import com.meession.etm.module.crm.dal.dataobject.quotation.CrmQuotationProductDO;
import com.meession.etm.module.crm.dal.mysql.quotation.CrmQuotationMapper;
import com.meession.etm.module.crm.dal.mysql.quotation.CrmQuotationProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - CRM 报价单")
@RestController
@RequestMapping("/crm/quotation")
@Validated
public class CrmQuotationController {

    @Resource private CrmQuotationMapper quotationMapper;
    @Resource private CrmQuotationProductMapper productMapper;

    @PostMapping("/create")
    @Operation(summary = "创建报价单")
    @PreAuthorize("@ss.hasPermission('crm:quotation:create')")
    public CommonResult<Long> create(@RequestBody Map<String, Object> body) {
        CrmQuotationDO q = new CrmQuotationDO();
        q.setBusinessId(Long.valueOf(body.get("businessId").toString()));
        q.setBusinessName((String) body.get("businessName")); q.setCustomerId(Long.valueOf(body.get("customerId").toString()));
        q.setCustomerName((String) body.get("customerName")); q.setTotalPrice(new java.math.BigDecimal(body.get("totalPrice").toString()));
        q.setDiscountPercent(new java.math.BigDecimal(body.getOrDefault("discountPercent", "100").toString()));
        q.setRemark((String) body.get("remark")); q.setAuditStatus(0);
        q.setOwnerUserId(Long.valueOf(body.get("ownerUserId").toString()));
        quotationMapper.insert(q);
        List<Map<String, Object>> products = (List<Map<String, Object>>) body.get("products");
        if (products != null) {
            for (Map<String, Object> p : products) {
                CrmQuotationProductDO prod = new CrmQuotationProductDO(); prod.setQuotationId(q.getId());
                prod.setProductId(Long.valueOf(p.get("productId").toString())); prod.setProductName((String) p.get("productName"));
                prod.setProductNo((String) p.get("productNo")); prod.setProductUnit((String) p.get("productUnit"));
                prod.setProductPrice(new java.math.BigDecimal(p.get("productPrice").toString()));
                prod.setCount(Integer.valueOf(p.get("count").toString()));
                prod.setTotalPrice(new java.math.BigDecimal(p.get("totalPrice").toString()));
                productMapper.insert(prod);
            }
        }
        return success(q.getId());
    }

    @GetMapping("/page")
    @Operation(summary = "报价单分页")
    @PreAuthorize("@ss.hasPermission('crm:quotation:query')")
    public CommonResult<PageResult<CrmQuotationDO>> page(@Valid PageParam pageParam) {
        return success(quotationMapper.selectPage(pageParam));
    }

    @GetMapping("/get")
    @Operation(summary = "报价单详情")
    @PreAuthorize("@ss.hasPermission('crm:quotation:query')")
    public CommonResult<CrmQuotationDO> get(@RequestParam Long id) { return success(quotationMapper.selectById(id)); }

    @GetMapping("/products")
    @Operation(summary = "报价单产品列表")
    @PreAuthorize("@ss.hasPermission('crm:quotation:query')")
    public CommonResult<List<CrmQuotationProductDO>> products(@RequestParam Long quotationId) {
        return success(productMapper.selectByQuotationId(quotationId));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除报价单")
    @PreAuthorize("@ss.hasPermission('crm:quotation:delete')")
    public CommonResult<Boolean> delete(@RequestParam Long id) { quotationMapper.deleteById(id); return success(true); }
}
