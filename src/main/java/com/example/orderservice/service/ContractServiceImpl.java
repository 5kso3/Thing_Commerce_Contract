package com.example.orderservice.service;

import com.example.orderservice.client.ClientDto;
import com.example.orderservice.client.ClientServiceFeignClient;
import com.example.orderservice.client.ItemDto;
import com.example.orderservice.client.ItemServiceFeignClient;
import com.example.orderservice.dto.ContractDto;
import com.example.orderservice.jpa.ContractEntity;
import com.example.orderservice.jpa.ContractRepository;
import feign.Contract;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ContractServiceImpl implements  ContractService{

    private ContractRepository contractRepository;
    private ClientServiceFeignClient clientServiceFeignClient;
    private ItemServiceFeignClient itemServiceFeignClient;

    @Autowired
    public ContractServiceImpl(ContractRepository contractRepository, ClientServiceFeignClient clientServiceFeignClient, ItemServiceFeignClient itemServiceFeignClient){
        this.contractRepository = contractRepository;
        this.clientServiceFeignClient = clientServiceFeignClient;
        this.itemServiceFeignClient = itemServiceFeignClient;
    }

    @Override
    public ContractDto getContract(long id) {
        ContractEntity contractEntity = contractRepository.findById(id).get();
        return this.setContractDto(contractEntity);
    }

    @Override
    public void createContract(int clientIndex, int itemId) {
        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setClientIndex(clientIndex);
        contractEntity.setItemId(itemId);
        contractEntity.setCreateAt(new Date());
        contractRepository.save(contractEntity);
    }

    @Override
    public List<ContractDto> getContractListByClientIndex(int clientIndex) {
        List<ContractEntity> contractEntityList = contractRepository.findByClientIndex(clientIndex);
        return contractEntityList.stream().map(contractEntity -> this.setContractDto(contractEntity)).collect(Collectors.toList());
    }

    private ContractDto setContractDto(ContractEntity contractEntity){
        ContractDto contractDto = new ContractDto();
        contractDto.setId(contractEntity.getId());
        contractDto.setCreateAt(contractEntity.getCreateAt());
        ClientDto clientDto = clientServiceFeignClient.getClient(contractEntity.getClientIndex()).getData();
        contractDto.setClient(clientDto);
        ItemDto itemDto = itemServiceFeignClient.getItem(contractEntity.getItemId()).getData();
        contractDto.setItem(itemDto);
        return contractDto;
    }

}
