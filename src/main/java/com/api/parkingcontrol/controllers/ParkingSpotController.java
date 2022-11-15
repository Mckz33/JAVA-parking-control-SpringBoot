package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600) //permite que o controller seja acessado de qualquer fonte
@RequestMapping("/parking-spot") //@RequestMapping - define URL a nivel de classe
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping// URL a nivel de class ja foi definida assim PostMapping busca do @RequestMapping
    // ResponseEntity - cria uma resposta com diferentes tipos de retornos
    //sabeParkingSpot - recebe o ParkingSpotDpo que verifica a escrita do usuario que retorna um JSON
    //@RequestBody - recebe dados via JSON
    //@Valid - é obrigatório e valida os dados recebidos no parametro que tende a acionar o metodo, caso o usuario esqueça de colocar um campo recebe um bad request
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto parkingSpotDto){

        if (parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if (parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }
        if (parkingSpotService.existsByApartmentAndBlock(parkingSpotDto.getApartment(), parkingSpotDto.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this apartment/block!");
        }

        //inicia um stancia
        var parkingSpotModel = new ParkingSpotModel();
        // converte o Dto em model
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel); // faz uma conversão de Dto para model
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));// seta a data de registro captada no momento do cadastro
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel)); // retorna a resposta
    }
}