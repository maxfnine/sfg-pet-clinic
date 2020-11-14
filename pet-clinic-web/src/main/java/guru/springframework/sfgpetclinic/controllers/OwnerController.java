package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/owners")
public class OwnerController {
    public static final String OWNERS_CREATE_OR_UPDATE_OWNER_FORM = "owners/createOrUpdateOwnerForm";
    public static final String OWNERS_FIND_OWNERS = "owners/findOwners";
    public static final String OWNERS_OWNER_DETAILS = "owners/ownerDetails";
    public static final String OWNERS_OWNERS_LIST = "owners/ownersList";
    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @RequestMapping("/find")
    public  String findOwners(Model model){
        model.addAttribute("owner", Owner.builder().build());
        return OWNERS_FIND_OWNERS;
    }

    @GetMapping
    public String processFindForm(Owner owner, BindingResult result,Model model){
        if(owner.getLastName()==null){
            owner.setLastName("");
        }
        List<Owner> owners = ownerService.findAllByLastNameLike("%"+owner.getLastName()+"%");
        if(owners.isEmpty()){
            result.rejectValue("lastName","notFound","not found");
            return OWNERS_FIND_OWNERS;
        }
        else if(owners.size()==1){
            return "redirect:/owners/"+owners.get(0).getId();
        }else{
            model.addAttribute("selections",owners);
            return OWNERS_OWNERS_LIST;
        }
    }


    @GetMapping("/{ownerId}")
    public ModelAndView showOwner(@PathVariable Long ownerId){
        ModelAndView modelAndView = new ModelAndView(OWNERS_OWNER_DETAILS);
        modelAndView.addObject("owner",ownerService.findById(ownerId));
        return modelAndView;
    }

    @GetMapping("/new")
    public String showCreateOwnerForm(Model model){
        model.addAttribute("owner",Owner.builder().build());
        return OWNERS_CREATE_OR_UPDATE_OWNER_FORM;
    }

    @PostMapping("/new")
    public String processCreateOwnerForm(Owner owner,BindingResult result){

        if(result.hasErrors()){
            return  OWNERS_CREATE_OR_UPDATE_OWNER_FORM;
        }else{
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/"+savedOwner.getId();
        }
    }

    @GetMapping("/{ownerId}/edit")
    public String showUpdateOwner(@PathVariable Long ownerId,Model model){
        model.addAttribute("owner",ownerService.findById(ownerId));
        return OWNERS_CREATE_OR_UPDATE_OWNER_FORM;
    }

    @PostMapping("/{ownerId}/edit")
    public String processCreateUpdateForm(@PathVariable Long ownerId,Owner owner,BindingResult result){
        if(result.hasErrors()){
            return OWNERS_CREATE_OR_UPDATE_OWNER_FORM;
        }else{
            owner.setId(ownerId);
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/"+ownerId;
        }
    }
}
