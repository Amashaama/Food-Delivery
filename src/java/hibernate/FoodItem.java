/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hibernate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "fooditem")
public class FoodItem implements Serializable{

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return the qty
     */
    public int getQty() {
        return qty;
    }

    /**
     * @param qty the qty to set
     */
    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * @return the created_at
     */
    public Date getCreated_at() {
        return created_at;
    }

    /**
     * @param created_at the created_at to set
     */
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the quality
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    /**
     * @return the portionSize
     */
    public PortionSize getPortionSize() {
        return portionSize;
    }

    /**
     * @param portionSize the portionSize to set
     */
    public void setPortionSize(PortionSize portionSize) {
        this.portionSize = portionSize;
    }

    /**
     * @return the spiceLevel
     */
    public SpiceLevel getSpiceLevel() {
        return spiceLevel;
    }

    /**
     * @param spiceLevel the spiceLevel to set
     */
    public void setSpiceLevel(SpiceLevel spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the subCategory
     */
    public SubCategory getSubCategory() {
        return subCategory;
    }

    /**
     * @param subCategory the subCategory to set
     */
    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public FoodItem() {
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "title",nullable = false)
    private String title;
    @Column(name="description",nullable = false)
    private String description;
     @Column(name="price",nullable = false)
    private Double price;
     @Column(name="qty",nullable = false)
   private int qty;
      @Column(name="created_at",nullable = false)
    private Date created_at;
    @ManyToOne
    @JoinColumn(name="status_id")
    private Status status;
     @ManyToOne
    @JoinColumn(name="quality_id")
    private Quality quality;
      @ManyToOne
    @JoinColumn(name="portion_size_id")
    private PortionSize portionSize;
       @ManyToOne
    @JoinColumn(name="spice_level_id")
    private SpiceLevel spiceLevel;
        @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
         @ManyToOne
    @JoinColumn(name="sub_category_id")
    private SubCategory subCategory;
    
}
