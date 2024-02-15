package edu.ucsb.cs156.example.controllers;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j
public class UCSBOrganizationsController extends ApiController {

    @Autowired
    UCSBOrganizationsRepository ucsbOrganizationsRepository;

    @Operation(summary= "List all ucsb organizations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBOrganizations> allOrganizations() {
        Iterable<UCSBOrganizations> organization = ucsbOrganizationsRepository.findAll();
        return organization;
    }

    @Operation(summary= "Create a new organizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBOrganizations postCommons(
        @Parameter(name="orgCode") @RequestParam String orgCode,
        @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
        @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
        @Parameter(name="inactive") @RequestParam boolean inactive)
        {
        UCSBOrganizations commons = new UCSBOrganizations();
        commons.setOrgCode(orgCode);
        commons.setOrgTranslationShort(orgTranslationShort);
        commons.setOrgTranslation(orgTranslation);
        commons.setInactive(inactive);

        UCSBOrganizations savedOrganizations = ucsbOrganizationsRepository.save(commons);

        return savedOrganizations;
    }

    @Operation(summary= "Get a single organization")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBOrganizations getById(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations organization = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));

        return organization;
    }

    @Operation(summary= "Delete a UCSBOrganizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteOrganizations(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganizations commons = ucsbOrganizationsRepository.findById(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, orgCode));

        ucsbOrganizationsRepository.delete(commons);
        return genericMessage("UCSBOrganizations with id %s deleted".formatted(orgCode));
    }

    @Operation(summary= "Update a single UCSBOrganization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganizations updateOrganizations(
            @Parameter(name="code") @RequestParam String code,
            @RequestBody @Valid UCSBOrganizations incoming) {

        UCSBOrganizations organization = ucsbOrganizationsRepository.findById(code)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganizations.class, code));

        organization.setOrgCode(incoming.getOrgCode());
        organization.setOrgTranslationShort(incoming.getOrgTranslationShort());
        organization.setOrgTranslation(incoming.getOrgTranslation());
        organization.setInactive(incoming.getInactive());

        ucsbOrganizationsRepository.save(organization);

        return organization;
    }

}
//last