package amnil.ims.service.product;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ProductResponse;

public interface IProductService {
    ProductResponse saveProduct(ProductRequest request);
}
