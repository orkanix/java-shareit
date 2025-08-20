package ru.practicum.shareit;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.item.service.ItemService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemServiceIntegrationTest {

	@Autowired
	private UserService userService;

	@Autowired
	private ItemService itemService;

	private UserDto savedUser;
	private ItemDto savedItemDto;

	@BeforeEach
	void setUp() {
		String email = "user" + System.currentTimeMillis() + "@mail.ru";

		NewUserDto newUserDto = NewUserDto.builder()
				.name("User")
				.email(email)
				.build();

		savedUser = userService.createUser(newUserDto);

		NewItemDto newItemDto = NewItemDto.builder()
				.name("TestName")
				.description("TestDescription")
				.available(true)
				.build();

		savedItemDto = itemService.createItem(newItemDto, savedUser.getId());
	}

	@Test
	@Transactional
	void testCreateItem() {
		NewItemDto newItemDto = NewItemDto.builder()
				.name("CreateTestName")
				.description("CreateTestDescription")
				.available(true)
				.build();

		ItemDto createdItem = itemService.createItem(newItemDto, savedUser.getId());

		assertNotNull(createdItem, "Созданная вещь не должна быть null");
		assertEquals(newItemDto.getName(), createdItem.getName(), "Имена должны совпадать");
		assertEquals(newItemDto.getDescription(), createdItem.getDescription(), "Описания должны совпадать");
		assertEquals(newItemDto.getAvailable(), createdItem.getAvailable(), "Доступность должна совпадать");
		assertNotNull(createdItem.getId(), "У созданной вещи должен быть id");
	}

	@Test
	@Transactional
	void testGetItem() {
		ItemBookingsDto itemDto = itemService.getItem(savedItemDto.getId());

		assertNotNull(itemDto);
		assertEquals("TestName", itemDto.getName());
		assertEquals("TestDescription", itemDto.getDescription());
		assertTrue(itemDto.getAvailable());
	}

	@Test
	@Transactional
	void testUpdateItem() {
		UpdateItemDto updateItemDto = UpdateItemDto.builder()
				.name("UpdatedName")
				.description("UpdatedDescription")
				.available(false)
				.build();

		ItemDto updatedItemDto = itemService.updateItem(updateItemDto, savedUser.getId(), savedItemDto.getId());

		assertEquals("UpdatedName", updatedItemDto.getName());
		assertEquals("UpdatedDescription", updatedItemDto.getDescription());
		assertFalse(updatedItemDto.getAvailable());
	}
}
