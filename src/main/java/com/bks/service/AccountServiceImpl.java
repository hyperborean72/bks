package com.bks.service;

import com.bks.domain.Account;
import com.bks.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
//@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	BigDecimal balanceRaiseLimit = new BigDecimal(2.07);
	BigDecimal coefficient = new BigDecimal(1.1);

	List<Account> initAccounts;
	List<BigDecimal>  finalBalanceList;

	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;

		initAccounts = getAccounts();
		finalBalanceList = initAccounts.stream()
		.map(account -> account.getBalance().multiply(balanceRaiseLimit))
		.collect(Collectors.toList());

		initAccounts.forEach(account->
				log.info("client ID - {}, initial  balance - {},  started at - {}", account.getClient().getId(), account.getBalance().setScale(2, RoundingMode.HALF_UP), formatEventDate()));
	}

	@Override
	public List<Account> getAccounts() {
		return accountRepository.findAll();
	};

	@Scheduled(fixedRateString = "${scheduler.interval}", initialDelayString =  "${scheduler.interval}")
	@Async
	@Override
	public void incrementBalance() {

		// список обновляемый каждые 30 с, поскольку на предыдущей итерации изменены балансы
		List<Account> accounts = getAccounts();

		accounts.forEach(account-> {
			BigDecimal newBalance = account.getBalance().multiply(coefficient);
			if (!(newBalance.compareTo(finalBalanceList.get(accounts.indexOf(account))) > 0)){
				account.setBalance(newBalance);
				log.info("client ID - {}, current  balance - {},  changed at - {}", account.getClient().getId(), account.getBalance().setScale(2, RoundingMode.HALF_UP), formatEventDate());
			}
		});
	}

	private  String formatEventDate(){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
		return formatter.format(LocalDateTime.now());
	}
}