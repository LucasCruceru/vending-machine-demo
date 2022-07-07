package com.example.vendingmachine.service;

import com.example.vendingmachine.dto.ProductUpdateRequestDto;
import com.example.vendingmachine.model.Product;
import com.example.vendingmachine.model.User;
import com.example.vendingmachine.repository.ProductRepository;
import com.example.vendingmachine.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

	@Autowired
	ProductRepository repository;
	@Autowired
	UserService userService;

	@PreAuthorize("hasAuthority('SELLER')")
	public Product create(Product product) {
		User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());
		validateCost(product.getCost());
		product.setUser(user);
		return repository.save(product);
	}

	public List<Product> get() {
		return repository.findAll();
	}

	@PreAuthorize("hasAuthority('SELLER')")
	public Product update(ProductUpdateRequestDto requestProduct, long productId) {
		User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());
		Optional<Product> optionalProduct = repository.findByIdAndUser(productId, user);

		if (optionalProduct.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with that id for your user!");

		var product = optionalProduct.get();

		updateProduct(requestProduct, product);

		return repository.save(product);
	}

	@PreAuthorize("hasAuthority('SELLER')")
	public void delete(long productId) {
		User user = userService.getUserByUsername(SecurityUtils.getAuthUsername());
		Optional<Product> optionalProduct = repository.findByIdAndUser(productId, user);

		if (optionalProduct.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no product with that id for your user!");

		repository.delete(optionalProduct.get());

	}

	private void updateProduct(ProductUpdateRequestDto requestProduct, Product product) {
		if(requestProduct.getName() != null)
			product.setName(requestProduct.getName());

		if (requestProduct.getCost() != null){
			validateCost(requestProduct.getCost());
			product.setCost(requestProduct.getCost());
		}

		if (requestProduct.getAmountAvailable() != null)
			product.setAmountAvailable(requestProduct.getAmountAvailable());
	}

	private void validateCost(int cost) {
		if (!(cost % 5 == 0))
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Cost should be divisible by 5");
	}
}