<script lang="ts">
  import Bar from '$lib/components/bar.svelte';
  import Terminal from '$lib/components/terminal.svelte';
  import * as Table from '$lib/components/ui/table';
  import { OptionTerminal } from '$lib/model/enum';
  import type { Secret } from '$lib/model/Secret';
  import { secretAPI } from '$lib/service/secret-service';
  import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
  import * as yaml from 'yaml';
  import Button from '$lib/components/ui/button/button.svelte';
  import IconKebabMenu from '$lib/components/icons/IconKebabMenu.svelte';
  import Pagination from '$lib/components/pagination.svelte';

  export let namespace;

  let size: number = 5;

  let loading = false;
  let loadingTable = false;
  let option: OptionTerminal;
  let details: string;

  let secrets: Secret[] = [];
  let paginated: Secret[] = [];
  let k8sItem: string;
  let open: boolean;

  $: if (namespace) {
    getAllSecrets();
  }

  const getAllSecrets = async () => {
    loadingTable = true;
    secrets = await secretAPI.getAllSecrets(namespace);
    loadingTable = false;
  };

  const getDetails = async (secret: string, opt: OptionTerminal): Promise<string> => {
    loading = true;
    open = true;
    k8sItem = secret;
    option = opt;
    details = await secretAPI.getSecretDetails(namespace, k8sItem);
    loading = false;
    return details;
  };

  const updateItem = async (secret: string) => {
    loading = true;
    details = await secretAPI.updateSecret(namespace, k8sItem, yaml.parse(secret));
    loading = false;
    return details;
  };
</script>

<div class="flex flex-col" style="height: calc(100vh - 150px);">
  <div class="flex-grow flex flex-col gap-8 justify-between overflow-auto">
    <Table.Root>
      <Table.Header>
        <Table.Row>
          <Table.Head>NAME</Table.Head>
          <Table.Head>TYPE</Table.Head>
          <Table.Head>DATA</Table.Head>
          <Table.Head>AGE</Table.Head>
          <Table.Head>Options</Table.Head>
        </Table.Row>
      </Table.Header>
      <Table.Body>
        {#if loadingTable}
          <Table.Row>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
            <Table.Cell><Bar /></Table.Cell>
          </Table.Row>
        {:else}
          {#each paginated as secret}
            <Table.Row
              on:click={() => {
                getDetails(secret.name, OptionTerminal.DETAIL);
              }}
              class="cursor-pointer"
            >
              <Table.Cell>{secret.name}</Table.Cell>
              <Table.Cell>{secret.type}</Table.Cell>
              <Table.Cell>{secret.data_size}</Table.Cell>
              <Table.Cell>{secret.age}</Table.Cell>
              <Table.Cell>
                <DropdownMenu.Root>
                  <DropdownMenu.Trigger>
                    <Button class="p-2 h-auto" variant="ghost">
                      <IconKebabMenu />
                    </Button>
                  </DropdownMenu.Trigger>
                  <DropdownMenu.Content align="end">
                    <DropdownMenu.Group>
                      <DropdownMenu.Item
                        onclick={() => {
                          getDetails(secret.name, OptionTerminal.DETAIL);
                        }}>View</DropdownMenu.Item
                      >
                      <DropdownMenu.Item
                        onclick={() => {
                          getDetails(secret.name, OptionTerminal.EDIT);
                        }}>Edit</DropdownMenu.Item
                      >
                      <DropdownMenu.Item>Delete</DropdownMenu.Item>
                    </DropdownMenu.Group>
                  </DropdownMenu.Content>
                </DropdownMenu.Root>
              </Table.Cell>
            </Table.Row>
          {/each}
        {/if}
      </Table.Body>
    </Table.Root>
    <div class="mb-5">
      <Pagination bind:pageSize={size} data={secrets} bind:paginated={paginated} />
    </div>
  </div>

  {#if k8sItem}
    <Terminal
      type="secret"
      getDetails={getDetails}
      updateItem={updateItem}
      k8sItem={k8sItem}
      option={option}
      details={details}
      loading={loading}
      bind:open={open}
    />
  {/if}
</div>
