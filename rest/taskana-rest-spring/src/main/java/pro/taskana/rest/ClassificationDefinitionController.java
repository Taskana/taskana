package pro.taskana.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.Classification;
import pro.taskana.ClassificationQuery;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationResourceAssembler;

/**
 * Controller for Importing / Exporting classifications.
 */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class ClassificationDefinitionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationDefinitionController.class);

    private ClassificationService classificationService;

    private ClassificationResourceAssembler classificationResourceAssembler;

    ClassificationDefinitionController(
        ClassificationService classificationService,
        ClassificationResourceAssembler classificationResourceAssembler) {
        this.classificationService = classificationService;
        this.classificationResourceAssembler = classificationResourceAssembler;
    }

    @GetMapping(path = Mapping.URL_CLASSIFICATIONDEFINITION)
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<List<ClassificationResource>> exportClassifications(
        @RequestParam(required = false) String domain)
        throws ClassificationNotFoundException {
        LOGGER.debug("Entry to exportClassifications(domain= {})", domain);
        ClassificationQuery query = classificationService.createClassificationQuery();

        List<ClassificationSummary> summaries = domain != null ? query.domainIn(domain).list() : query.list();
        List<ClassificationResource> export = new ArrayList<>();

        for (ClassificationSummary summary : summaries) {
            Classification classification = classificationService.getClassification(summary.getKey(),
                summary.getDomain());

            export.add(classificationResourceAssembler.toDefinition(classification));
        }

        ResponseEntity<List<ClassificationResource>> response = ResponseEntity.ok(export);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from exportClassifications(), returning {}", response);
        }

        return response;
    }

    @PostMapping(path = Mapping.URL_CLASSIFICATIONDEFINITION)
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Void> importClassifications(
        @RequestParam("file") MultipartFile file)
        throws InvalidArgumentException, NotAuthorizedException, ConcurrencyException, ClassificationNotFoundException,
        ClassificationAlreadyExistException, DomainNotFoundException, IOException {
        LOGGER.debug("Entry to importClassifications()");
        Map<String, String> systemIds = getSystemIds();
        List<ClassificationResource> classificationsResources = extractClassificationResourcesFromFile(file);
        checkForDuplicates(classificationsResources);

        Map<Classification, String> childrenInFile = mapChildrenToParentKeys(classificationsResources, systemIds);
        insertOrUpdateClassificationsWithoutParent(classificationsResources, systemIds);
        updateParentChildrenRelations(childrenInFile);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();
        LOGGER.debug("Exit from importClassifications(), returning {}", response);
        return response;
    }

    private Map<String, String> getSystemIds() {
        Map<String, String> systemIds = classificationService.createClassificationQuery()
            .list()
            .stream()
            .collect(Collectors.toMap(i -> i.getKey() + "|" + i.getDomain(), ClassificationSummary::getId));
        return systemIds;
    }

    private List<ClassificationResource> extractClassificationResourcesFromFile(MultipartFile file)
        throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ClassificationResource> classificationsDefinitions = mapper.readValue(file.getInputStream(),
            new TypeReference<List<ClassificationResource>>() {

            });
        return classificationsDefinitions;
    }

    private void checkForDuplicates(List<ClassificationResource> classificationList) {
        List<String> identifiers = new ArrayList<>();
        Set<String> duplicates = new HashSet<>();
        for (ClassificationResource classification : classificationList) {
            String identifier = classification.key + "|" + classification.domain;
            if (identifiers.contains(identifier)) {
                duplicates.add(identifier);
            } else {
                identifiers.add(identifier);
            }
        }
        if (!duplicates.isEmpty()) {
            throw new DuplicateKeyException(
                "The 'key|domain'-identifier is not unique for the value(s): " + duplicates.toString());
        }
    }

    private Map<Classification, String> mapChildrenToParentKeys(List<ClassificationResource> classificationResources,
        Map<String, String> systemIds) {
        LOGGER.debug("Entry to mapChildrenToParentKeys()");
        Map<Classification, String> childrenInFile = new HashMap<>();
        Set<String> newKeysWithDomain = new HashSet<>();
        classificationResources.forEach(cl -> newKeysWithDomain.add(cl.getKey() + "|" + cl.getDomain()));

        for (ClassificationResource cl : classificationResources) {
            cl.parentId = cl.parentId == null ? "" : cl.parentId;
            cl.parentKey = cl.parentKey == null ? "" : cl.parentKey;

            if (!cl.getParentId().equals("") && cl.getParentKey().equals("")) {
                for (ClassificationResource parent : classificationResources) {
                    if (cl.getParentId().equals(parent.getClassificationId())) {
                        cl.setParentKey(parent.getKey());
                    }
                }
            }

            String parentKeyAndDomain = cl.parentKey + "|" + cl.domain;
            if (!cl.getParentKey().isEmpty() && !cl.getParentKey().equals("")) {
                if (newKeysWithDomain.contains(parentKeyAndDomain) || systemIds.containsKey(parentKeyAndDomain)) {
                    childrenInFile.put(classificationResourceAssembler.toModel(cl), cl.getParentKey());
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from mapChildrenToParentKeys(), returning {}", LoggerUtils.mapToString(childrenInFile));
        }

        return childrenInFile;
    }

    private void insertOrUpdateClassificationsWithoutParent(List<ClassificationResource> classificationResources,
        Map<String, String> systemIds)
        throws ClassificationNotFoundException, NotAuthorizedException, InvalidArgumentException,
        ClassificationAlreadyExistException, DomainNotFoundException, ConcurrencyException {
        LOGGER.debug("Entry to insertOrUpdateClassificationsWithoutParent()");

        for (ClassificationResource classificationResource : classificationResources) {
            classificationResource.setParentKey(null);
            classificationResource.setParentId(null);
            classificationResource.setClassificationId(null);

            String systemId = systemIds.get(classificationResource.key + "|" + classificationResource.domain);
            if (systemId != null) {
                updateExistingClassification(classificationResource, systemId);
            } else {
                classificationService.createClassification(
                    classificationResourceAssembler.toModel(classificationResource));
            }
        }
        LOGGER.debug("Exit from insertOrUpdateClassificationsWithoutParent()");
    }

    private void updateParentChildrenRelations(Map<Classification, String> childrenInFile)
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException,
        InvalidArgumentException {
        LOGGER.debug("Entry to updateParentChildrenRelations()");

        for (Map.Entry<Classification, String> entry : childrenInFile.entrySet()) {
            Classification childRes = entry.getKey();
            String parentKey = entry.getValue();
            String classificationKey = childRes.getKey();
            String classificationDomain = childRes.getDomain();

            Classification child = classificationService.getClassification(classificationKey, classificationDomain);
            String parentId = (parentKey == null) ? ""
                : classificationService.getClassification(parentKey, classificationDomain).getId();

            child.setParentKey(parentKey);
            child.setParentId(parentId);

            classificationService.updateClassification(child);
        }
        LOGGER.debug("Exit from updateParentChildrenRelations()");
    }

    private void updateExistingClassification(ClassificationResource cl,
        String systemId) throws ClassificationNotFoundException, NotAuthorizedException,
        ConcurrencyException, InvalidArgumentException {
        LOGGER.debug("Entry to updateExistingClassification()");
        Classification currentClassification = classificationService.getClassification(systemId);
        if (cl.getType() != null && !cl.getType().equals(currentClassification.getType())) {
            throw new InvalidArgumentException("Can not change the type of a classification.");
        }
        currentClassification.setCategory(cl.category);
        currentClassification.setIsValidInDomain(cl.isValidInDomain);
        currentClassification.setName(cl.name);
        currentClassification.setParentId(cl.parentId);
        currentClassification.setParentKey(cl.parentKey);
        currentClassification.setDescription(cl.description);
        currentClassification.setPriority(cl.priority);
        currentClassification.setServiceLevel(cl.serviceLevel);
        currentClassification.setApplicationEntryPoint(cl.applicationEntryPoint);
        currentClassification.setCustom1(cl.custom1);
        currentClassification.setCustom2(cl.custom2);
        currentClassification.setCustom3(cl.custom3);
        currentClassification.setCustom4(cl.custom4);
        currentClassification.setCustom5(cl.custom5);
        currentClassification.setCustom6(cl.custom6);
        currentClassification.setCustom7(cl.custom7);
        currentClassification.setCustom8(cl.custom8);
        classificationService.updateClassification(currentClassification);
        LOGGER.debug("Exit from updateExistingClassification()");
    }

}
