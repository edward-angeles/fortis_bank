package ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import bus.AlertService;
import bus.AlertTypeService;
import bus.CustomerService;
import bus.NotInRangeException;
import bus.NotNullException;
import bus.TransactionService;
import bus.TransactionTypeService;
import dto.AccountDTO;
import dto.AlertDTO;
import dto.AlertType;
import dto.CustomerDTO;
import dto.TransactionDTO;
import dto.TransactionType;

public class testAccountLoading {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		TransactionService tSvc = new TransactionService();
		TransactionTypeService transTypeSvc = new TransactionTypeService();
		AlertTypeService alertTypeSvc = new AlertTypeService();
		AlertService alertSvc = new AlertService();
		
		try {		
		
		 AlertType type = alertTypeSvc.getById(1);
         
         AlertDTO alert = new AlertDTO(null, "Deposit of 200 succesfull", type, null);
         alertSvc.create(alert);

         // Get transaction type by id
         TransactionType ttype = transTypeSvc.getById(1);
         
         TransactionDTO tx = new TransactionDTO(null, ttype, 200, LocalDate.now(), null, "2", 0.0, alert);
         
         tSvc.create(tx);
         
         tSvc.getAll();
         
		for (TransactionDTO acc: tSvc.getAll()) {
			System.out.println(acc);
		}
		
		
		
		} catch(Exception e) {
			throw new Exception("There is a mistake");
		}
		
	}

}
