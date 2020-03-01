package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.api.model.DeleteAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.StatesList;
import com.upgrad.FoodOrderingApp.api.model.StatesListResponse;
import com.upgrad.FoodOrderingApp.service.business.AddressService;
import com.upgrad.FoodOrderingApp.service.business.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// Address Controller Handles all  the Address related endpoints

@CrossOrigin
@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    AddressService addressService; //Handles all service related to the Address

    @Autowired
    CustomerService customerService; // Handles all the Service Related to the Customer.


    /* The method handles Address save Related request.It takes the details as per in the SaveAddressRequest
     & produces response in SaveAddressResponse and returns UUID of newly Created Customer Address and Success message else Return error code and error Message.
      */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST,path = "/address",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization, @RequestBody(required = false)SaveAddressRequest saveAddressRequest)throws AuthorizationFailedException, AddressNotFoundException, SaveAddressException {

        //Access the accessToken from the request Header
      String accessToken = customerService.getBearerAccessToken(authorization);

        //Calls customerService getCustomer Method to check the validity of the customer.this methods returns the customerEntity  to be updated  with address.
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //Creating addressEntity from SaveAddressRequest data.
        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());


        //Getting stateEntity using address service method getStateByUUID.
        StateEntity stateEntity = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        //Calls saveAddress method to save the new addressEntity Created.
        AddressEntity savedAddress = addressService.saveAddress(addressEntity,stateEntity);

        //Calls saveCustomerAddressEntity method to save to save foreign Keys of the newly created address and the Customer.
        CustomerAddressEntity customerAddressEntity = addressService.saveCustomerAddressEntity(customerEntity,savedAddress);

        //Creating SaveAddressResponse response
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddress.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse,HttpStatus.CREATED);
    }

    /*  The method handles get all Address  request.It takes the authorization
     & produces response in AddressListResponse and returns list of Customer Address .If error Return error code and error Message.
      */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/address/customer",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization")final String authorization)throws AuthorizationFailedException{

        //Access the accessToken from the request Header
      String accessToken = customerService.getBearerAccessToken(authorization);

        //Calls customerService getCustomer Method to check the validity of the customer.this methods returns the customerEntity  to be get all address.
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //Calls getAllAddress method of addressService to get list of address
        List<AddressEntity> addressEntities = addressService.getAllAddress(customerEntity);
        Collections.reverse(addressEntities); //Reversing the list to show last saved as first.

        //Creating list of  AddressList using the Model AddressList & this List will be added to AddressListResponse
        List<AddressList> addressLists = new LinkedList<>();
        //Looping in for each address in the addressEntities & then Created AddressList using the each address data and adds to addressLists.
        addressEntities.forEach(addressEntity -> {
            AddressListState addressListState = new AddressListState()
                    .stateName(addressEntity.getState().getStateName())
                    .id(UUID.fromString(addressEntity.getState().getStateUuid()));
            AddressList addressList = new AddressList()
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .city(addressEntity.getCity())
                    .flatBuildingName(addressEntity.getFlatBuilNo())
                    .locality(addressEntity.getLocality())
                    .pincode(addressEntity.getPincode())
                    .state(addressListState);
            addressLists.add(addressList);
        });

        //Creating the AddressListResponse by adding the addressLists.
        AddressListResponse addressListResponse = new AddressListResponse().addresses(addressLists);
        return new ResponseEntity<AddressListResponse>(addressListResponse,HttpStatus.OK);
    }

    /*  The method handles delete  Address  request.It takes the authorization and path variables address UUID
  & produces response in DeleteAddressResponse and returns UUID of deleted address and Successfull message .If error Return error code and error Message.
   */
  @CrossOrigin
    @RequestMapping(method = RequestMethod.DELETE,path = "/address/{address_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(
      @RequestHeader("authorization") final String authorization,
      @PathVariable(value = "address_id") final String addressUuid)
      throws AuthorizationFailedException, AddressNotFoundException {

        //Access the accessToken from the request Header
    String accessToken = customerService.getBearerAccessToken(authorization);

        //Calls customerService getCustomer Method to check the validity of the customer.this methods returns the customerEntity of whose address be deleted.
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        //Calls getAddressByUUID method of addressService to get the address by uuid
        AddressEntity addressEntity = addressService.getAddressByUUID(addressUuid,customerEntity);

        //Calls deleteAddress method of addressService to delete the address from DB
        AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);

        //Creating DeleteAddressResponse containing UUID of deleted address and Successful Message.
        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressEntity.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse,HttpStatus.OK);


    }

    /*  The method handles States request.It produces response in StatesListResponse and returns UUID & stateName .If error Return error code and error Message.
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET,path = "/states",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates(){

        //Calls getAllStates method in addressService and returns list of stateEntity.
        List<StateEntity> stateEntities = addressService.getAllStates();

        if(!stateEntities.isEmpty()) {//Checking if StateEntities is empty.
            //Creates List of StateList using Model StateList.
            List<StatesList> statesLists = new LinkedList<>();
            //looping in to get details of all the the stateEntity & then create a stateList and add UUID of state and stateName and add the newly created StateList to the list of StateList.
            stateEntities.forEach(stateEntity -> {
                StatesList statesList = new StatesList()
                        .id(UUID.fromString(stateEntity.getStateUuid()))
                        .stateName(stateEntity.getStateName());
                statesLists.add(statesList);
            });

            //Creating StatesListResponse & adding list of stateLists
            StatesListResponse statesListResponse = new StatesListResponse().states(statesLists);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        }else
            //Return empty set if stateEntities is empty.
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(),HttpStatus.OK);
    }


}