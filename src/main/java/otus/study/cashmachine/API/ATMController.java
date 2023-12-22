package otus.study.cashmachine.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import otus.study.cashmachine.machine.data.CashMachine;
import otus.study.cashmachine.machine.service.CashMachineService;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ATMController {

    private final CashMachineService cashMachineService;
    private final CashMachine cashMachine;

    @Autowired
    public ATMController(CashMachineService cashMachineService, CashMachine cashMachine) {
        this.cashMachineService = cashMachineService;
        this.cashMachine = cashMachine;
    }

    @GetMapping("/getMoney")
    public String showПуеMoneyForm(Model model) {
        model.addAttribute("atmRequest", new ATMRequest());
        return "GetMoney";
    }

    @PostMapping("/getMoney")
    public String getMoney(@ModelAttribute ATMRequest atmRequest, Model model
    ) {
        try {
            List<Integer> money = cashMachineService.getMoney(cashMachine, atmRequest.getCardNum(), atmRequest.getPin(), atmRequest.getAmount());
            model.addAttribute("notes5000", money.get(0));
            model.addAttribute("notes1000", money.get(1));
            model.addAttribute("notes500", money.get(2));
            model.addAttribute("notes100", money.get(3));
            return "GetMoney";
        } catch (Exception e) {
            return "error";
        }
    }


    @GetMapping("/putMoney")
    public String showPutMoneyForm(Model model) {
        model.addAttribute("atmRequest", new ATMRequest());
        return "PutMoney";
    }

    @PostMapping("/putMoney")
    public String putMoney(@ModelAttribute ATMRequest atmRequest, Model model) {
        try {
            BigDecimal balance = cashMachineService.putMoney(cashMachine, atmRequest.getCardNum(), atmRequest.getPin(), List.of(atmRequest.getCount5000(),
                    atmRequest.getCount1000(), atmRequest.getCount500(), atmRequest.getCount100()));

            model.addAttribute("balance", balance);
            return "PutMoney";
        } catch (Exception e) {
            return "error";
        }
    }


    @GetMapping("/checkBalance")
    public String checkBalance(
            @RequestParam(required = false) String cardNum,
            @RequestParam(required = false) String pin,
            Model model
    ) {
        try {
            if (cardNum != null && pin != null) {
                BigDecimal balance = cashMachineService.checkBalance(cashMachine, cardNum, pin);
                model.addAttribute("Balance", balance);
            }
            return "CheckBalance";
        } catch (Exception e) {
            return "error";
        }
    }

    @GetMapping("/changePin")
    public String showСhangePinForm(Model model) {
        model.addAttribute("atmRequest", new ATMRequest());
        return "ChangePin";
    }

    @PostMapping("/changePin")
    public String changePin(@ModelAttribute ATMRequest atmRequest, Model model
    ) {
        try {
            boolean pinChanged = cashMachineService.changePin(atmRequest.getCardNum(), atmRequest.getPin(), atmRequest.getNewPin());
            if (pinChanged) {
                model.addAttribute("message", "Pin successfully changed");
                return "ChangePin";
            }
        } catch (Exception e) {
            return "error";
        }
        return "error";
    }

}
