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
@Table(name = "questions")
public class Questions implements Serializable {

    /**
     * @return the question_status
     */
    public String getQuestion_status() {
        return question_status;
    }

    /**
     * @param question_status the question_status to set
     */
    public void setQuestion_status(String question_status) {
        this.question_status = question_status;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer the answer to set
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

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
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
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
     * @return the foodItem
     */
    public FoodItem getFoodItem() {
        return foodItem;
    }

    /**
     * @param foodItem the foodItem to set
     */
    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
    }

    public Questions() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "question", nullable = false)
    private String question;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "foodItem_id", nullable = false)
    private FoodItem foodItem;
    
    @Column(name = "answer",nullable = false)
    private String answer;
    
    @Column(name = "date",nullable = false)
    private Date date;
    
    @Column(name ="question_status" ,length = 20,nullable = false)
    private String question_status;

}
