package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "ucsborganizations")
@RequestMapping("/api/ucsorganizations")
@RestController
public class UCSBOrganizationsController extends ApiController {

    @Autowired
    UCSBOrganizationsRepository ucsbOrganizationsRepository;

    @Operation(summary = "List all UCSB organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allOrganizations() {
        return ucsbOrganizationsRepository.findAll();
    }

    @Operation(summary = "Create a new organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postOrganization(
            @Parameter(name = "orgCode") @RequestParam String orgCode,
            @Parameter(name = "orgTranslationShort") @RequestParam String orgTranslationShort,
            @Parameter(name = "orgTranslation") @RequestParam String orgTranslation,
            @Parameter(name = "inactive") @RequestParam boolean inactive) {

        UCSBOrganizations organization = new UCSBOrganizations();
        organization.setOrgCode(orgCode);
        organization.setOrgTranslationShort(orgTranslationShort);
        organization.setOrgTranslation(orgTranslation);
        organization.setInactive(inactive);

        return ucsbOrganizationsRepository.save(organization);
    }

    @Operation(summary = "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getById(
            @Parameter(name = "id") @RequestParam Long id) {
        return ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));
    }

    @Operation(summary = "Delete a UCSBOrganizations entry")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public ResponseEntity<?> deleteOrganization(
            @Parameter(name = "id") @RequestParam Long id) {
        UCSBOrganizations organization = ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));

        ucsbOrganizationsRepository.delete(organization);
        return ResponseEntity.ok().body(String.format("UCSBOrganizations with id %s deleted", id));
    }

     @Operation(summary = "Update a single organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateOrganization(
            @Parameter(name = "id") @RequestParam Long id,
            @RequestBody @Valid UCSBOrganizations incoming) {

        UCSBOrganizations organization = ucsbOrganizationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, id));

        organization.setOrgCode(incoming.getOrgCode());
        organization.setOrgTranslationShort(incoming.getOrgTranslationShort());
        organization.setOrgTranslation(incoming.getOrgTranslation());
        organization.setInactive(incoming.getInactive());

        return ucsbOrganizationsRepository.save(organization);
    }
}