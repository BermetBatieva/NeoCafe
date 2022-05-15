package com.example.backend.service;

import com.example.backend.Enums.Status;
import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.exception.AlreadyExistsException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompositionRepository compositionRepository;

    @Autowired
    private GeneralAdditionalRepository generalAdditionalRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    //получение всех блюд из меню по категориям
    public List<Menu> getAllStocksByCategory(Long id) throws Exception {
        return menuRepository.findAllByCategoryAndStatus(
                categoryRepository.findById(id).orElseThrow(
                        () -> new Exception("Категория с такой id  не существует! id = "+ id))
                , Status.ACTIVATE
        );
    }

    //удаление меню по id
    @Transactional
    public DeletedDTO deleteById(Long id) throws Exception {
        Menu menu = menuRepository.findById(id).orElseThrow(
                () -> new Exception("меню с такой id не существует! id = "+ id));
        menu.setName("deleted name: " + menu.getName());
        menu.setStatus(Status.DELETED);
        menuRepository.save(menu);
        return new DeletedDTO(id);
    }

    //обновление меню
    @Transactional
    public MenuForAdminDTO update(MenuUpdateDTO menuDTO) throws Exception {
        Menu menu = menuRepository.findById(menuDTO.getId()).orElseThrow(
                () -> new Exception("меню с такой id не существует! id = "+ menuDTO.getId())
        );
        menu.setName(menuDTO.getName());
        menu.setCategory(categoryRepository.getById(menuDTO.getCategoryId()));
        menu.setDescription(menuDTO.getDescription());

        List<Composition> compositionList = compositionRepository.findAllById(menuDTO.getDeleteComposition());
        compositionList.forEach(c -> c.setStatus(Status.DELETED));
        compositionRepository.saveAll(compositionList);

        List<GeneralAdditional> generalAdditionalList = generalAdditionalRepository.findAllById(menuDTO.getDeleteGeneralAdditional());
        generalAdditionalList.forEach(g -> g.setStatus(Status.DELETED));
        generalAdditionalRepository.saveAll(generalAdditionalList);

        //добавление нового состава
        addCompositionAndGeneralAdditional(menuDTO.getComposition(), menuDTO.getGeneralAdditional(), menu.getId());

        return MenuToString(menu);
    }

    //Первая страница амдина(Список всех Меню )
    public List<MenuForAdminDTO> allMenuForAdmin(Long idCategory) throws Exception {
        List<MenuForAdminDTO> menuDtoList = new ArrayList<>();
        List<Menu> menuList = menuRepository.findAllByStatusAndAndCategory(Status.ACTIVATE,
                categoryRepository.findById(idCategory).
                        orElseThrow(() -> new Exception("Категория с такой id не существует! id = "+
                                idCategory)));
        for (Menu menu : menuList) {
            menuDtoList.add(MenuToString(menu));
        }
        return menuDtoList;
    }

    private MenuForAdminDTO MenuToString(Menu menu) {
        MenuForAdminDTO menuDto = new MenuForAdminDTO();
        menuDto.setId(menu.getId());
        menuDto.setMenuName(menu.getName());
        menuDto.setCategory(menu.getCategory().getName());
        List<Composition> compositionList = compositionRepository.findAllByMenu(menu);
        List<GeneralAdditional> generalAdditionalList = generalAdditionalRepository.findAllByMenu(menu);
        List<String> nameCompositions = new ArrayList<>();

        //Собирает весь состав блюда в "List<String> nameCompositions"
        for (Composition composition : compositionList) {
            nameCompositions.add((composition.getProductId().getProductName()) +
                    " (" + (composition.getQuantity()) + composition.getProductId().getUnit() + ")");
        }
        for (GeneralAdditional generalAdditional : generalAdditionalList) {
            nameCompositions.add((generalAdditional.getWarehouse().getProductName()) +
                    " (" + (generalAdditional.getQuantity()) + generalAdditional.getWarehouse().getUnit() + ")");
        }
        menuDto.setComposition(nameCompositions);
        menuDto.setPrice(menu.getPrice());
        return menuDto;
    }

    //Добавление меню
    public MenuDTO add(MenuDTO menuDTO) throws Exception {
        if (menuRepository.existsByName(menuDTO.getName())) {
            throw new Exception("Меню с таким именем уже существует!");
        } else {
            Menu menu = new Menu();
            return saverMenu(menu, menuDTO);
        }
    }

    @Transactional
    MenuDTO saverMenu(Menu menu, MenuDTO menuDTO) throws Exception {
        menu.setName(menuDTO.getName());
        menu.setCategory(categoryRepository.findById(menuDTO.getCategoryId())
                .orElseThrow(() -> new Exception("категория с такой id не найдено! id = "+
                        menuDTO.getCategoryId())));
        menu.setDescription(menuDTO.getDescription());
        menu.setPrice(menuDTO.getPrice());
        menu.setCounter(0l);
        menu.setImagesUrl("");
        menu.setStatus(Status.ACTIVATE);
        menuRepository.save(menu);
        Menu menuId = menuRepository.getByName(menu.getName()).orElseThrow();
        long id = menuId.getId();
        menuDTO.setId(id);
        //добавление составов блюда
        List<CompositionDTO> compositions = new ArrayList<>(menuDTO.getComposition());
        List<GeneralAdditionalDTO> generalAdditionalDTOS = new ArrayList<>(menuDTO.getGeneralAdditional());
        addCompositionAndGeneralAdditional(compositions, generalAdditionalDTOS, id);

        return menuDTO;
    }

    //приватный метод который добавляет выбранные состав в меню
    private void addCompositionAndGeneralAdditional(List<CompositionDTO> compositions,
                                                    List<GeneralAdditionalDTO> generalAdditionalDTOS, Long id) throws Exception {
        for (CompositionDTO composition1 : compositions) {
            Warehouse warehouse = warehouseRepository.findById(composition1.getProductId())
                    .orElseThrow(() -> new Exception(
                            "Composition с такой id не найден! id = " + composition1.getProductId()));
            Composition composition2 = new Composition();
            composition2.setProductId(warehouse);
            composition2.setMenu(menuRepository.getById(id));
            composition2.setStatus(Status.ACTIVATE);
            composition2.setQuantity(composition1.getQuantity());
            compositionRepository.save(composition2);
        }
        for (GeneralAdditionalDTO generalAdditional1 : generalAdditionalDTOS) {
            Warehouse warehouse2 = warehouseRepository.findById(generalAdditional1.getProductId())
                    .orElseThrow(() -> new Exception(
                            "GenerealAdditional с такой id не найден! id = "+ generalAdditional1.getProductId()
                    ));
            GeneralAdditional generalAdditional2 = new GeneralAdditional();
            generalAdditional2.setQuantity(generalAdditional1.getQuantity());
            generalAdditional2.setWarehouse(warehouse2);
            generalAdditional2.setTypeGeneralAdditional(generalAdditional1.getTypeGeneral());
            generalAdditional2.setPrice(generalAdditional1.getPrice());
            generalAdditional2.setStatus(Status.ACTIVATE);
            generalAdditional2.setMenu(menuRepository.getById(id));
            generalAdditionalRepository.save(generalAdditional2);
        }
    }

    //TODO: After menu table in the db of heroku will be full change:
    // menuRepository.findAllTopStock() -> menuRepository.findTop10ByOrderByCounterDesc()
    public List<Menu> topStock() {
        return menuRepository.findTop3ByOrderByCounterDesc();
    }

    public List<MenuForWaiter> allMenuByCategoryForWaiter(long categoryId) throws Exception {
        List<Menu> list = menuRepository.findAllByCategoryAndStatus(
                categoryRepository.findById(categoryId).orElseThrow(
                        () -> new Exception("Категория с такой id  не существует! id = "+ categoryId))
                , Status.ACTIVATE);

        List<MenuForWaiter> result = new ArrayList<>();
        for (Menu menu : list) {
            MenuForWaiter model = new MenuForWaiter();
            model.setMenuId(menu.getId());
            model.setName(menu.getName());
            model.setPrice(menu.getPrice());
            result.add(model);
        }
        return result;
    }

    public List<MenuForClient> allMenuByCategoryForClient(long categoryId) throws Exception {
        List<Menu> list = menuRepository.findAllByCategoryAndStatus(
                categoryRepository.findById(categoryId).orElseThrow(
                        () -> new Exception("Категория с такой id  не существует! id = "+ categoryId))
                , Status.ACTIVATE);

        List<MenuForClient> result = new ArrayList<>();
        for (Menu menu : list) {
            MenuForClient model = new MenuForClient();
            model.setId(menu.getId());
            model.setName(menu.getName());
            model.setDescription(menu.getDescription());
            model.setImageUrl(menu.getImagesUrl());
            model.setGeneralAdditional(convertToGeneralDTO(generalAdditionalRepository.findAllByMenu(menu)));
            model.setPrice(menu.getPrice());
            result.add(model);
        }
        return result;
    }

    public List<MenuForBarista> allMenuByCategoryForBarista(long categoryId) throws Exception {
        List<Menu> list = menuRepository.findAllByCategoryAndStatus(
                categoryRepository.findById(categoryId).orElseThrow(
                        () -> new Exception("Категория с такой id  не существует! id = "+ categoryId))
                , Status.ACTIVATE
        );
        List<MenuForBarista> result = new ArrayList<>();
        for (Menu menu : list) {
            MenuForBarista model = new MenuForBarista();
            List<Composition> compositionList = compositionRepository.findAllByMenu(menu);
            List<GeneralAdditional> generalAdditionalList = generalAdditionalRepository.findAllByMenu(menu);

            model.setFlag(!generalAdditionalList.isEmpty());
            List<GeneralDTO>  generalDTOS = new ArrayList<>();
            for (GeneralAdditional g : generalAdditionalList) {
                GeneralDTO generalAdditionalDTO = new GeneralDTO();
                generalAdditionalDTO.setId(g.getId());
                generalAdditionalDTO.setNameProduct(g.getWarehouse().getProductName());
                generalAdditionalDTO.setPrice(g.getPrice());
                generalAdditionalDTO.setTypeGeneralAdditional(g.getTypeGeneralAdditional());
                generalDTOS.add(generalAdditionalDTO);
            }
            model.setGeneralList(generalDTOS);
            List<String> nameCompositions = new ArrayList<>();
            for (Composition composition : compositionList) {
                nameCompositions.add((composition.getProductId().getProductName()) + " "
                        + (composition.getQuantity()) + composition.getProductId().getUnit());
            }
            model.setImage(menu.getImagesUrl());
            model.setComposition(nameCompositions);
            model.setName(menu.getName());
            model.setPrice(menu.getPrice());
            model.setId(menu.getId());

            result.add(model);
        }
        return result;
    }

    public MenuByID getInfoById(Long id) throws Exception {
        Menu menu = menuRepository.findById(id).orElseThrow(
                () -> new Exception("menu с такой id нет, id = "+ id));

        MenuByID menuDTO = new MenuByID();

        menu.setId(menu.getId());
        menuDTO.setImage(menu.getImagesUrl());
        menuDTO.setMenuName(menu.getName());
        menuDTO.setDescription(menu.getDescription());
        List<Composition> compositionList = compositionRepository.findAllByMenu(menu);

        List<String> nameCompositions = new ArrayList<>();


        for (Composition composition : compositionList) {
            nameCompositions.add((composition.getProductId().getProductName()) + " "
                    + (composition.getQuantity()) + composition.getProductId().getUnit());
        }
        menuDTO.setComposition(nameCompositions);

        return menuDTO;
    }

    public List<GeneralDTO> getGeneral(Long id) throws Exception {
        List<GeneralAdditional> generalAdditionalList = generalAdditionalRepository.findAllByMenu(
                menuRepository.findById(id).orElseThrow(
                        () -> new Exception("Меню с такой id не существует!"))
        );
        return convertToGeneralDTO(generalAdditionalList);
    }

    private List<GeneralDTO> convertToGeneralDTO(List<GeneralAdditional> generalAdditionalList) {
        List<GeneralDTO> result = new ArrayList<>();
        for (GeneralAdditional g : generalAdditionalList) {
            GeneralDTO generalDTOForClient = new GeneralDTO();
            generalDTOForClient.setId(g.getId());
            generalDTOForClient.setPrice(g.getPrice());
            generalDTOForClient.setTypeGeneralAdditional(g.getTypeGeneralAdditional());
            generalDTOForClient.setNameProduct(g.getWarehouse().getProductName());
            result.add(generalDTOForClient);
        }
        return result;
    }


    public MenuForClient getOneMenuForAdmin(Long id) throws Exception {
        Menu menu = menuRepository.findById(id).orElseThrow(
                () -> new Exception("Меню с такой id не существует!")
        );
        MenuForClient menuForClient = new MenuForClient();
        menuForClient.setId(menu.getId());
        menuForClient.setName(menu.getName());
        menuForClient.setDescription(menu.getDescription());
        menuForClient.setImageUrl(menu.getImagesUrl());
        menuForClient.setGeneralAdditional(convertToGeneralDTO(generalAdditionalRepository.findAllByMenu(menu)));
        return menuForClient;
    }


}



