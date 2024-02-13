package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UCSBOrganizationsRepository extends CrudRepository<UCSBOrganizations, Long> {
        Iterable<UCSBOrganizations> findByOrgTranslationShort(String orgTranslationShort);

}