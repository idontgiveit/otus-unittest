package otus.study.cashmachine.machine.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import otus.study.cashmachine.TestUtil;
import otus.study.cashmachine.bank.dao.CardsDao;
import otus.study.cashmachine.bank.data.Card;
import otus.study.cashmachine.bank.service.AccountService;
import otus.study.cashmachine.bank.service.CardService;
import otus.study.cashmachine.bank.service.impl.CardServiceImpl;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.data.MoneyBox;
import otus.study.cashmachine.machine.service.impl.CashMachineServiceImpl;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashMachineServiceTest {

    @Spy
    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardsDao cardsDao;

    @Mock
    private AccountService accountService;

    @Mock
    private MoneyBoxService moneyBoxService;

    private CashMachineServiceImpl cashMachineService;

    private CashMachine cashMachine = new CashMachine(new MoneyBox());

    @BeforeEach
    void init() {
        cashMachineService = new CashMachineServiceImpl(cardService, accountService, moneyBoxService);
    }


    @Test
    void getMoney() {
// @TODO create get money test using spy as mock
    }

    @Test
    void putMoney() {
    }

    @Test
    void checkBalance() {

    }

    @Test
    void changePin() {
// @TODO create change pin test using spy as implementation and ArgumentCaptor and thenReturn

        when(cardsDao.getCardByNumber("1111"))
                .thenReturn(new Card(1, "1111", 1L, "0000"));

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        when(cardsDao.saveCard(captor.capture())).thenReturn(new Card(1, "1", 1L, "1"));

        cashMachineService.changePin("1111", "0000", "0001");
        Assertions.assertEquals(TestUtil.getHash("0001"), captor.getValue().getPinCode());

//        String oldPin = "1234";
//        String cardNum = "88002323";
//        Card testedCard = new Card(1L, cardNum, 10L, TestUtil.getHash(oldPin));
//
//        when(cardsDao.getCardByNumber(cardNum)).thenReturn(testedCard);
//
//        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
//        when(cardsDao.saveCard(captor.capture())).thenReturn(null);
//
//        String newPin = "9999";
//        cashMachineService.changePin(cardNum, oldPin, newPin);
//        Assertions.assertEquals(captor.getValue().getPinCode(), TestUtil.getHash(newPin));
    }

    @Test
    void changePinWithAnswer() {
// @TODO create change pin test using spy as implementation and mock an thenAnswer
    }
}