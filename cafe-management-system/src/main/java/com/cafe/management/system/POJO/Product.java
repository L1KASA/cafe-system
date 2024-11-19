package com.cafe.management.system.POJO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(
        name = "Product.getAllProduct",
        query = "select new com.cafe.management.system.wrapper.ProducWrapper(p.id, p.name,p.description,p.price,p.status,p.category.id,p.category.name) from Product p where p.status = 'true'"
)

@NamedQuery(
        name = "Product.updateProductStatus",
        query = "update Product p set p.status=:status where p.id=:id"
)

@NamedQuery(
        name = "Product.getProductByCategory",
        query = "select new com.cafe.management.system.wrapper.ProducWrapper(p.id, p.name) from Product p where p.category.id=:id and p.status='true'"
)

@NamedQuery(
        name = "Product.getProductById",
        query = "select new com.cafe.management.system.wrapper.ProducWrapper(p.id, p.name,p.description,p.price) from Product p where p.id=:id"
)

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
public class Product implements Serializable {

    public static final Long serialVersionUID = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne()
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;
}
