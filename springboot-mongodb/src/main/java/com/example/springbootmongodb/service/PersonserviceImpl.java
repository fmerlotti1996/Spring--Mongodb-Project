package com.example.springbootmongodb.service;

import com.example.springbootmongodb.collection.Person;
import com.example.springbootmongodb.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;



import javax.management.Query;
import javax.swing.text.Document;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonserviceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public PersonserviceImpl(PersonRepository personRepository, MongoTemplate mongoTemplate) {
        this.personRepository = personRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public String save(Person person) {
        return personRepository.save(person).getPersonId();
    }

    @Override
    public List<Person> getPersonStartWith(String name) {
        return personRepository.findByFirstNameStartsWith(name);
    }

    @Override
    public void delete(String id) {
        personRepository.deleteById(id);

    }

    @Override
    public List<Person> getByPersonAge(Integer minAge, Integer maxAge) {
        return personRepository.findByAgeBetween(minAge,maxAge);
    }

    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        org.springframework.data.mongodb.repository.Query query = null;
        //query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if(name != null && !name.isEmpty()) {
            criteria.add(Criteria.where("firstName").regex(name,"i"));
        }

        if(minAge != null && maxAge != null)
        {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if (city != null && !city.isEmpty()) {
            criteria.add(Criteria.where("adresses.city").is(city));
        }

        //if(!criteria.isEmpty()) {
         //   query.addCriteria(new Criteria()
         //           .andOperator(criteria.toArray(new Criteria[0])));
       // }

        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find((org.springframework.data.mongodb.core.query.Query) query,Person.class),pageable,
                () -> mongoTemplate.count(((org.springframework.data.mongodb.core.query.Query) query).skip(0).limit(0),Person.class));
        return people;
    }

    @Override
    public List<Document> getOlderPersonbyCity() {
        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC,"age");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city").first(Aggregation.ROOT).as("oldestPerson");

        Aggregation aggregation
                =Aggregation.newAggregation(unwindOperation,sortOperation,groupOperation);

        List<Document> person
                =mongoTemplate.aggregate(aggregation,Person.class, Document.class).getMappedResults();
        return person;
    }

    @Override
    public List<Document> getPopuplationByCiy() {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("populationCount");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC,"populationCount");
        ProjectionOperation projectionOperation
                =Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("populationCount").as("count")
                .andExclude("_id");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);

        List<Document> documents
                =mongoTemplate.aggregate(aggregation,
                Person.class,
                Document.class).getMappedResults();


        return documents;
    }


}
