package com.sogoodlabs.planner.controllers;

import com.sogoodlabs.planner.model.entities.Mean;
import com.sogoodlabs.planner.services.RepetitionsPlannerService;
import com.sogoodlabs.planner.services.GracefulDeleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 27.04.2018.
 */

@RestController
@RequestMapping(path = "/task")
public class TasksRESTController {



}
