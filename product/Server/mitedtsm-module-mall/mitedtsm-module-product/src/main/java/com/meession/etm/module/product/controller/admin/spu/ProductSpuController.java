package com.meession.etm.module.product.controller.admin.spu;

import com.meession.etm.framework.apilog.core.annotation.ApiAccessLog;
import com.meession.etm.framework.common.pojo.CommonResult;
import com.meession.etm.framework.common.pojo.PageResult;
import com.meession.etm.framework.common.util.object.BeanUtils;
import com.meession.etm.framework.excel.core.util.ExcelUtils;
import com.meession.etm.module.product.controller.admin.spu.vo.*;
import com.meession.etm.module.product.convert.spu.ProductSpuConvert;
import com.meession.etm.module.product.dal.dataobject.sku.ProductSkuDO;
import com.meession.etm.module.product.dal.dataobject.spu.ProductSpuDO;
import com.meession.etm.module.product.enums.spu.ProductSpuStatusEnum;
import com.meession.etm.module.product.service.sku.ProductSkuService;
import com.meession.etm.module.product.service.spu.ProductSpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.meession.etm.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.meession.etm.framework.common.pojo.CommonResult.success;
import static com.meession.etm.framework.common.pojo.PageParam.PAGE_SIZE_NONE;

@Tag(name = "管理后台 - 商品 SPU")
@RestController
@RequestMapping("/product/spu")
@Validated
public class ProductSpuController {

    @Resource
    private ProductSpuService productSpuService;
    @Resource
    private ProductSkuService productSkuService;

    @PostMapping("/create")
    @Operation(summary = "创建商品 SPU")
    @PreAuthorize("@ss.hasPermission('product:spu:create')")
    public CommonResult<Long> createProductSpu(@Valid @RequestBody ProductSpuSaveReqVO createReqVO) {
        return success(productSpuService.createSpu(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新商品 SPU")
    @PreAuthorize("@ss.hasPermission('product:spu:update')")
    public CommonResult<Boolean> updateSpu(@Valid @RequestBody ProductSpuSaveReqVO updateReqVO) {
        productSpuService.updateSpu(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新商品 SPU Status")
    @PreAuthorize("@ss.hasPermission('product:spu:update')")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody ProductSpuUpdateStatusReqVO updateReqVO) {
        productSpuService.updateSpuStatus(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除商品 SPU")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:spu:delete')")
    public CommonResult<Boolean> deleteSpu(@RequestParam("id") Long id) {
        productSpuService.deleteSpu(id);
        return success(true);
    }

    @GetMapping("/get-detail")
    @Operation(summary = "获得商品 SPU 明细")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<ProductSpuRespVO> getSpuDetail(@RequestParam("id") Long id) {
        // 获得商品 SPU
        ProductSpuDO spu = productSpuService.getSpu(id);
        if (spu == null) {
            return success(null);
        }
        // 查询商品 SKU
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spu.getId());
        return success(ProductSpuConvert.INSTANCE.convert(spu, skus));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获得商品 SPU 精简列表")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<List<ProductSpuSimpleRespVO>> getSpuSimpleList() {
        List<ProductSpuDO> list = productSpuService.getSpuListByStatus(ProductSpuStatusEnum.ENABLE.getStatus());
        // 降序排序后，返回给前端
        list.sort(Comparator.comparing(ProductSpuDO::getSort).reversed());
        return success(BeanUtils.toBean(list, ProductSpuSimpleRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得商品 SPU 详情列表")
    @Parameter(name = "spuIds", description = "spu 编号列表", required = true, example = "[1,2,3]")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<List<ProductSpuRespVO>> getSpuList(@RequestParam("spuIds") Collection<Long> spuIds) {
        return success(ProductSpuConvert.INSTANCE.convertForSpuDetailRespListVO(
                productSpuService.getSpuList(spuIds), productSkuService.getSkuListBySpuId(spuIds)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得商品 SPU 分页")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<PageResult<ProductSpuRespVO>> getSpuPage(@Valid ProductSpuPageReqVO pageVO) {
        PageResult<ProductSpuDO> pageResult = productSpuService.getSpuPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ProductSpuRespVO.class));
    }

    @GetMapping("/get-count")
    @Operation(summary = "获得商品 SPU 分页 tab count")
    @PreAuthorize("@ss.hasPermission('product:spu:query')")
    public CommonResult<Map<Integer, Long>> getSpuCount() {
        return success(productSpuService.getTabsCount());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出商品")
    @PreAuthorize("@ss.hasPermission('product:spu:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSpuList(@Validated ProductSpuPageReqVO reqVO,
                               HttpServletResponse response) throws IOException {
        reqVO.setPageSize(PAGE_SIZE_NONE);
        List<ProductSpuDO> list = productSpuService.getSpuPage(reqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "商品列表.xls", "数据", ProductSpuRespVO.class,
                BeanUtils.toBean(list, ProductSpuRespVO.class));
    }

    // ==================== 批量操作 ====================

    @DeleteMapping("/batch-delete")
    @Operation(summary = "批量删除商品 SPU")
    @Parameter(name = "ids", description = "编号数组", required = true, example = "1,2,3")
    @PreAuthorize("@ss.hasPermission('product:spu:delete')")
    public CommonResult<Boolean> batchDeleteSpu(@RequestParam("ids") List<Long> ids) {
        productSpuService.batchDeleteSpu(ids);
        return success(true);
    }

    @PutMapping("/batch-update")
    @Operation(summary = "批量修改商品 SPU")
    @PreAuthorize("@ss.hasPermission('product:spu:update')")
    public CommonResult<Boolean> batchUpdateSpu(@Valid @RequestBody ProductSpuBatchUpdateReqVO reqVO) {
        productSpuService.batchUpdateSpu(reqVO);
        return success(true);
    }

    // ==================== 导入 ====================

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入商品模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        List<ProductSpuImportExcelVO> list = Arrays.asList(
                ProductSpuImportExcelVO.builder().name("示例商品A").keyword("夏季新品")
                        .introduction("舒适透气").description("详情描述")
                        .categoryId(null).brandId(null).picUrl("")
                        .sort(10).status(0)
                        .price(9900).marketPrice(19900).costPrice(5000).stock(100)
                        .barCode("BAR001").weight(1.0).volume(0.5)
                        .giveIntegral(10).virtualSalesCount(50).build(),
                ProductSpuImportExcelVO.builder().name("示例商品B").keyword("冬季保暖")
                        .introduction("加厚设计").description("详情描述")
                        .categoryId(null).brandId(null).picUrl("")
                        .sort(20).status(0)
                        .price(19900).marketPrice(29900).costPrice(10000).stock(200)
                        .barCode("BAR002").weight(2.0).volume(1.0)
                        .giveIntegral(20).virtualSalesCount(100).build()
        );
        ExcelUtils.write(response, "商品导入模板.xls", "商品列表", ProductSpuImportExcelVO.class, list);
    }

    @PostMapping("/import")
    @Operation(summary = "导入商品 SPU")
    @PreAuthorize("@ss.hasPermission('product:spu:create')")
    public CommonResult<String> importSpu(@RequestParam("file") MultipartFile file) throws Exception {
        List<ProductSpuImportExcelVO> list = ExcelUtils.read(file, ProductSpuImportExcelVO.class);
        productSpuService.importSpuList(list);
        return success("导入成功，共处理 " + list.size() + " 条数据");
    }

}
