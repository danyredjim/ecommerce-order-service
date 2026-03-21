package com.example.ecommerce_order_service.dto;

//import java.util.List;

public class CreateOrderRequest {

    private Long customerId;
    
    private Long productId;
    
    private OrderItemRequest items;

    //private List<OrderItemRequest> items;

    private String shippingAddress;
    
    private Integer quantity;
    

    // getters y setters
	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	
	/*public List<OrderItemRequest> getItems() {
		return items;
	}

	public void setItems(List<OrderItemRequest> items) {
		this.items = items;
	}*/

	public OrderItemRequest getItems() {
		return items;
	}

	public void setItems(OrderItemRequest items) {
		this.items = items;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

    
    
}
